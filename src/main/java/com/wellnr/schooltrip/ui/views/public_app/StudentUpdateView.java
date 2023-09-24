package com.wellnr.schooltrip.ui.views.public_app;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.*;
import com.wellnr.schooltrip.core.application.commands.schooltrip.RegisteredStudentViewCommand;
import com.wellnr.schooltrip.core.application.commands.students.CompleteOrUpdateStudentRegistrationCommand;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.public_app.HeadlineWithTitle;
import com.wellnr.schooltrip.ui.components.student.StudentRegistrationQuestionnaireControl;

@Route("/students/update/:token")
public class StudentUpdateView extends AbstractPublicAppView implements BeforeEnterObserver {

    private final ApplicationCommandRunner commandRunner;

    private StudentRegistrationQuestionnaireControl questionnaire;

    private Student student;

    public StudentUpdateView(
        ApplicationUserSession userSession, ApplicationCommandRunner commandRunner
    ) {
        super(userSession);
        this.commandRunner = commandRunner;
    }

    public static RouteParameters getRouteParameters(Student student) {
        return new RouteParameters(
            new RouteParam("token", student.getConfirmationToken())
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        var token = beforeEnterEvent
            .getRouteParameters()
            .get("token")
            .orElseThrow();

        var projection = commandRunner
            .run(RegisteredStudentViewCommand.apply(token))
            .getData();

        /*
         * Initialize view.
         */
        student = projection.student();
        schoolTrip = projection.schoolTrip();

        questionnaire = new StudentRegistrationQuestionnaireControl(
            i18n,
            projection.schoolTrip(),
            projection.student()
        );

        this.contentContainer.add(
            new HeadlineWithTitle(
                projection.schoolTrip().getTitle(),
                i18n.updateRegistrationHeadline(projection.student())
            ),
            new Paragraph(
                i18n.updateRegistrationDescription(projection.student())
            ),
            questionnaire,
            new SubmitSection()
        );
    }

    private class SubmitSection extends VerticalLayout {

        private final EmailField email;

        public SubmitSection() {
            this.setMargin(false);
            this.setPadding(false);

            this.email = new EmailField(i18n.email());


            add(new H4(i18n.confirmation()));

            var form = new FormLayout();
            form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
            form.add(email);

            var submit = new Button(i18n.save());
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
            var questionnaire = StudentUpdateView.this.questionnaire.getValue();

            var cmd = CompleteOrUpdateStudentRegistrationCommand.apply(
                student.getToken(), questionnaire, email.getValue()
            );

            commandRunner.run(cmd);

            UI.getCurrent().navigate(
                StudentRegisteredView.class,
                StudentUpdateView.getRouteParameters(student)
            );
        }

    }

}
