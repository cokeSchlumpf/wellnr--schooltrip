package com.wellnr.schooltrip.ui.components.student;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.shared.Registration;
import com.wellnr.common.markup.Tuple2;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.application.commands.students.UpdateStudentPropertiesCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.ui.StudentCompleteRegistrationView;
import com.wellnr.schooltrip.ui.components.ApplicationCopyTextField;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandForm;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandFormBuilder;

public class StudentBasicsControl extends VerticalLayout {

    private final String appBaseUrl;

    private final ApplicationCommandForm<MessageResult<Student>, UpdateStudentPropertiesCommand> form;

    private Student student;

    private ApplicationCopyTextField registrationLink;

    public StudentBasicsControl(String appBaseUrl, SchoolTrip schoolTrip, SchoolTripCommandRunner commandRunner) {
        this.appBaseUrl = appBaseUrl;

        form = new ApplicationCommandFormBuilder<>(
            UpdateStudentPropertiesCommand.class,
            commandRunner
        )
            .addVariant("schoolClass", ApplicationCommandFormBuilder.FormVariant.LINE_BREAK_AFTER)
            .setFieldPossibleValues(
                "schoolClass",
                schoolTrip
                    .getSchoolClasses()
                    .stream()
                    .map(cl -> Tuple2.apply(cl.getName(), cl.getName()))
                    .toList()
            )
            .build();

        form.addCompletionListener(event -> {
            fireEvent(new StudentBasicsUpdatedEvent(this, true, this.student));
        });

        registrationLink = new ApplicationCopyTextField();
        registrationLink.textField.setLabel("Student Registration Link");

        this.add(form);
        this.add(new Hr());
        this.add(registrationLink);
    }

    public Registration addBasicsUpdatedListener(
        ComponentEventListener<StudentBasicsUpdatedEvent> listener) {

        return addListener(StudentBasicsUpdatedEvent.class, listener);
    }

    public void setStudent(Student student) {
        this.student = student;

        this.form.setGetInitialCommand(
            () -> UpdateStudentPropertiesCommand.apply(student)
        );

        var href = new RouterLink(
            StudentCompleteRegistrationView.class,
            StudentCompleteRegistrationView.getRouteParameters(student)
        ).getHref();

        registrationLink.textField.setValue(appBaseUrl + href);
    }
}
