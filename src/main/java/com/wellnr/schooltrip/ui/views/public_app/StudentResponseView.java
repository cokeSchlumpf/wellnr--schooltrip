package com.wellnr.schooltrip.ui.views.public_app;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.wellnr.schooltrip.core.application.commands.schooltrip.CompleteStudentRegistrationViewCommand;
import com.wellnr.schooltrip.core.application.commands.students.RejectParticipationCommand;
import com.wellnr.schooltrip.core.model.student.RejectionReason;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.public_app.HeadlineWithTitle;

import java.util.Arrays;
import java.util.Objects;

@Route("/students/response/:token")
public class StudentResponseView extends AbstractPublicAppView implements BeforeEnterObserver {

    private static final String PARTICIPATE = "participate";

    private static final String DONT_PARTICIPATE__OUT_OF_SNOW = "out of snow";

    private static final String DONT_PARTICIPATE__SCHOOL = "school";

    private final ApplicationCommandRunner commandRunner;

    private Student student;

    private RadioButtonGroup<String> registrationOptions;

    private Button saveButton;

    public StudentResponseView(
        ApplicationUserSession userSession,
        ApplicationCommandRunner commandRunner
    ) {
        super(userSession);
        this.commandRunner = commandRunner;
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

        var introduction = new Div();
        introduction.addClassName("app__student-registration__introduction");
        introduction.add(Arrays
            .stream(i18n.studentResponseInfoText(student).split("<p>"))
            .map(p -> (Component) new Paragraph(p))
            .toList()
        );
        introduction.setWidthFull();

        registrationOptions = new RadioButtonGroup<>();
        registrationOptions.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        registrationOptions.setItems(
            PARTICIPATE,
            DONT_PARTICIPATE__OUT_OF_SNOW,
            DONT_PARTICIPATE__SCHOOL
        );
        registrationOptions.setItemLabelGenerator(item -> switch (item) {
            case PARTICIPATE -> i18n.participate(student);
            case DONT_PARTICIPATE__OUT_OF_SNOW -> i18n.dontParticipateOutOfSnow(student);
            case DONT_PARTICIPATE__SCHOOL -> i18n.dontParticipateSchool(student);
            default -> throw new IllegalStateException();
        });
        registrationOptions.addValueChangeListener(event -> updateView());

        if (student.getRejectionReason().isEmpty()) {
            registrationOptions.setValue(PARTICIPATE);
        } else if (student.getRejectionReason().isPresent()) {
            if (student.getRejectionReason().get().equals(RejectionReason.OUT_OF_SNOW)) {
                registrationOptions.setValue(DONT_PARTICIPATE__OUT_OF_SNOW);
            } else {
                registrationOptions.setValue(DONT_PARTICIPATE__SCHOOL);
            }
        }

        this.saveButton = new Button(i18n.save());
        this.saveButton.addClickListener(event -> submit());

        var form = new FormLayout();
        form.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("650px", 2)
        );
        form.add(registrationOptions);
        form.setColspan(registrationOptions, 2);

        var layout = new VerticalLayout();
        layout.add(new HeadlineWithTitle(
            projection.schoolTrip().getTitle(),
            i18n.registerStudentHeadline(student)
        ));
        layout.add(introduction);
        layout.add(form);
        layout.add(saveButton);
        layout.setPadding(false);
        layout.setMargin(false);

        this.contentContainer.add(layout);
        updateView();
    }

    private void updateView() {
        if (!(Objects.nonNull(registrationOptions) && Objects.nonNull(saveButton))) {
            return;
        }

        if (registrationOptions.getValue().equals(PARTICIPATE)) {
            this.saveButton.setText(i18n.nextButton());
        } else {
            this.saveButton.setText(i18n.save());
        }
    }

    private void submit() {
        if (registrationOptions.getValue().equals(PARTICIPATE)) {
            UI.getCurrent().navigate(
                StudentRegistrationView.class,
                StudentRegistrationView.getRouteParameters(student)
            );
        } else {
            var rejecttionReason = RejectionReason.OUT_OF_SNOW;

            if (registrationOptions.getValue().equals(DONT_PARTICIPATE__SCHOOL)) {
                rejecttionReason = RejectionReason.GO_TO_SCHOOL;
            }

            commandRunner.run(
                RejectParticipationCommand.apply(student.getToken(), rejecttionReason)
            );

            this.contentContainer.removeAll();
            this.contentContainer.add(new HeadlineWithTitle(
                schoolTrip.getTitle(),
                i18n.thankYou()
            ));

            this.contentContainer.add(new Paragraph(i18n.confirmRejectionMailText(
                schoolTrip, student, rejecttionReason
            )));
        }
    }

}
