package com.wellnr.schooltrip.ui.public_app;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.*;
import com.wellnr.schooltrip.core.application.commands.students.CompleteOrUpdateStudentRegistrationCommand;
import com.wellnr.schooltrip.core.application.commands.schooltrip.CompleteStudentRegistrationViewCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.public_app.HeadlineWithTitle;
import com.wellnr.schooltrip.ui.components.student.StudentRegistrationQuestionnaireControl;
import com.wellnr.schooltrip.ui.layout.AbstractPublicAppView;

import java.util.Arrays;

@PageTitle("School Trip")
@Route("/students/complete-registration/:token")
public class StudentRegistrationView extends AbstractPublicAppView implements BeforeEnterObserver {

    private final ApplicationCommandRunner commandRunner;

    private Student student;

    private SchoolTrip schoolTrip;

    private StudentRegistrationQuestionnaireControl questionnaire;

    private String emailAddress;

    public StudentRegistrationView(ApplicationCommandRunner commandRunner, ApplicationUserSession userSession) {
        super(userSession);
        this.commandRunner = commandRunner;
    }

    public static RouteParameters getRouteParameters(Student student) {
        return new RouteParameters(
            "token", student.getToken()
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        var token = beforeEnterEvent
            .getRouteParameters()
            .get("token")
            .orElseThrow();

        var projection = commandRunner
            .run(
                CompleteStudentRegistrationViewCommand.apply(token)
            )
            .getData();

        student = projection.student();
        schoolTrip = projection.schoolTrip();

        /*
         * Initialize view.
         */

        questionnaire = new StudentRegistrationQuestionnaireControl(i18n, schoolTrip, student);

        var introduction = new Div();
        introduction.addClassName("app__student-registration__introduction");
        introduction.add(Arrays
            .stream(i18n.registerStudentInfo(student).split("<p>"))
            .map(p -> (Component) new Paragraph(p))
            .toList()
        );

        var layout = new VerticalLayout();
        layout.add(new HeadlineWithTitle(
            projection.schoolTrip().getTitle(),
            i18n.registerStudentHeadline(student)
        ));
        layout.add(introduction);
        layout.add(questionnaire);
        layout.add(new SubmitSection());
        layout.setPadding(false);
        layout.setMargin(false);
        this.contentContainer.add(layout);
    }

    private void displayConfirmation() {
        var headline = new HeadlineWithTitle(
            schoolTrip.getTitle(),
            i18n.studentRegisteredTitle(student)
        );

        var infoText = new Paragraph(
            i18n.studentRegisteredText(student, emailAddress)
        );

        var button = new Button(i18n.backToRegistration());
        button.addClickListener(event -> {
            this.contentContainer.removeAll();

            UI.getCurrent().navigate(
                StudentRegistrationView.class,
                StudentRegistrationView.getRouteParameters(student)
            );
        });

        this.contentContainer.removeAll();
        this.contentContainer.add(headline);
        this.contentContainer.add(infoText);
        this.contentContainer.add(button);
    }

    private class SubmitSection extends VerticalLayout {

        private final EmailField email;

        public SubmitSection() {
            this.setMargin(false);
            this.setPadding(false);

            this.email = new EmailField(i18n.email());

            var info = new Paragraph(i18n.confirmationInfo());
            info.addClassName("app__student-registration__submit-section__info");

            add(new H4(i18n.confirmation()));
            add(info);

            var form = new FormLayout();
            form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
            form.add(email);

            var submit = new Button(i18n.submit());
            submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            submit.addClickShortcut(Key.ENTER);
            submit.setEnabled(false);
            email.addValueChangeListener(
                event -> submit.setEnabled(!event.getValue().isBlank() && !email.isInvalid())
            );
            submit.addClickListener(event -> this.submit());

            this.add(form);
            this.add(submit);
        }

        private void submit() {
            var questionnaire = StudentRegistrationView.this.questionnaire.getValue();

            var cmd = CompleteOrUpdateStudentRegistrationCommand.apply(
                student.getToken(), questionnaire, email.getValue()
            );

            emailAddress = email.getValue();
            commandRunner.run(cmd);
            displayConfirmation();
        }

    }

}
