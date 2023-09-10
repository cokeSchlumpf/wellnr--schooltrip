package com.wellnr.schooltrip.ui;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.*;
import com.wellnr.schooltrip.core.application.commands.schooltrip.CompleteStudentRegistrationCommand;
import com.wellnr.schooltrip.core.application.commands.schooltrip.CompleteStudentRegistrationViewCommand;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.ApplicationContentContainer;
import com.wellnr.schooltrip.ui.components.student.StudentRegistrationQuestionnaireControl;

@Route("/students/complete-registration/:token")
@PageTitle("School Trip")
public class StudentCompleteRegistrationView extends ApplicationContentContainer implements BeforeEnterObserver {

    private final SchoolTripMessages i18n;

    private final ApplicationCommandRunner commandRunner;

    private StudentRegistrationQuestionnaireControl questionnaire;

    private Student student;

    public StudentCompleteRegistrationView(ApplicationUserSession userSession, ApplicationCommandRunner commandRunner) {
        this.i18n = userSession.getMessages();
        this.commandRunner = commandRunner;
    }

    public static RouteParameters getRouteParameters(String token) {
        return new RouteParameters(new RouteParam("token", token));
    }

    public static RouteParameters getRouteParameters(Student student) {
        return getRouteParameters(student.getToken());
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
        var schoolTrip = projection.schoolTrip();

        /*
         * Initialize view.
         */

        questionnaire = new StudentRegistrationQuestionnaireControl(i18n, schoolTrip, student);

        var layout = new VerticalLayout();
        layout.add(new H3(i18n.registerStudentHeadline(student)));
        layout.add(new Paragraph(i18n.registerStudentInfo(student)));
        layout.add(questionnaire);
        layout.add(new SubmitSection());

        this.add(layout);
    }

    private class SubmitSection extends VerticalLayout {

        private final EmailField email;

        public SubmitSection() {
            this.email = new EmailField(i18n.email());

            add(new H4(i18n.confirmation()));
            add(new Paragraph(i18n.confirmationInfo()));

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
            var questionnaire = StudentCompleteRegistrationView.this.questionnaire.getValue();

            var cmd = CompleteStudentRegistrationCommand.apply(
                student.getToken(), questionnaire, email.getValue()
            );

            commandRunner.run(cmd);
        }

    }

}
