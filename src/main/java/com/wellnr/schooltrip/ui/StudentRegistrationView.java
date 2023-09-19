package com.wellnr.schooltrip.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.*;
import com.wellnr.schooltrip.core.application.commands.schooltrip.CompleteStudentRegistrationCommand;
import com.wellnr.schooltrip.core.application.commands.schooltrip.CompleteStudentRegistrationViewCommand;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.student.StudentRegistrationQuestionnaireControl;

import java.util.Arrays;

@PageTitle("School Trip")
@Route("/students/complete-registration/:token")
public class StudentRegistrationView extends Div implements BeforeEnterObserver {

    private final SchoolTripMessages i18n;

    private final ApplicationCommandRunner commandRunner;

    private final Div contentContainer;

    private Student student;

    private StudentRegistrationQuestionnaireControl questionnaire;

    public StudentRegistrationView(ApplicationCommandRunner commandRunner, ApplicationUserSession userSession) {
        this.i18n = userSession.getMessages();
        this.commandRunner = commandRunner;

        this.addClassName("app__student-registration");

        var logo = new Image("images/logo.png", "Ski- und Snowboard-Freizeit des GaS Merzig.");
        logo.addClassName("app__student-registration__logo__img");

        var logoContainer = new Div();
        logoContainer.addClassName("app__student-registration__logo");
        logoContainer.add(logo);

        this.contentContainer = new Div();
        contentContainer.addClassName("app__student-registration__content");

        this.add(logoContainer);
        this.add(contentContainer);
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
        var schoolTrip = projection.schoolTrip();

        /*
         * Initialize view.
         */

        questionnaire = new StudentRegistrationQuestionnaireControl(i18n, schoolTrip, student);

        var headlineHeaderTitle = new Span(projection.schoolTrip().getTitle());
        headlineHeaderTitle.addClassName("app__student-registration__headline__header-title");

        var headline = new H2(
            headlineHeaderTitle,
            new Span(i18n.registerStudentHeadline(student))
        );
        headline.addClassName("app__student-registration__headline");

        var introduction = new Div();
        introduction.addClassName("app__student-registration__introduction");
        introduction.add(Arrays
            .stream(i18n.registerStudentInfo(student).split("<p>"))
            .map(p -> (Component) new Paragraph(p))
            .toList()
        );

        var layout = new VerticalLayout();
        layout.add(headline);
        layout.add(introduction);
        layout.add(questionnaire);
        layout.add(new SubmitSection());
        this.contentContainer.add(layout);
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

            var cmd = CompleteStudentRegistrationCommand.apply(
                student.getToken(), questionnaire, email.getValue()
            );

            commandRunner.run(cmd);
        }

    }

}
