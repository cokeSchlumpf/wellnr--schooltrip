package com.wellnr.schooltrip.ui.components.student;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import com.wellnr.schooltrip.core.application.commands.schooltrip.CompleteOrUpdateStudentRegistrationByOrganizerCommand;
import com.wellnr.schooltrip.core.application.commands.schooltrip.ConfirmStudentRegistrationCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.student.RegistrationState;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;

import java.util.Objects;

public class StudentRegistrationAdminControl extends VerticalLayout {

    private final SchoolTrip schoolTrip;

    private final ApplicationCommandRunner commandRunner;

    private final Paragraph infoText;

    private final Button registerStudent;

    private final Button confirmRegistration;

    private final Button updateRegistration;

    private Student student;

    private StudentRegistrationQuestionnaireControl initialRegistration;

    private StudentRegistrationQuestionnaireControl existingRegistration;

    public StudentRegistrationAdminControl(SchoolTrip schoolTrip, ApplicationCommandRunner commandRunner) {
        this.schoolTrip = schoolTrip;
        this.commandRunner = commandRunner;

        this.infoText = new Paragraph();
        this.registerStudent = new Button("Register student");

        this.registerStudent.addClickListener(event -> {
            var cmd = CompleteOrUpdateStudentRegistrationByOrganizerCommand.apply(
                this.student.getId(), initialRegistration.getValue()
            );

            this.commandRunner.runAndNotify(cmd).ifPresent(i -> fireEvent(
                new StudentRegistrationUpdatedEvent(this, true, this.student)
            ));
        });

        this.confirmRegistration = new Button("Confirm Registration");
        this.confirmRegistration.addClickListener(e -> {
            var cmd = ConfirmStudentRegistrationCommand.apply(student.getConfirmationToken());
            this.commandRunner.runAndNotify(cmd).ifPresent(i -> fireEvent(
                new StudentRegistrationUpdatedEvent(this, true, this.student)
            ));
        });

        this.updateRegistration = new Button("Update registration details");
        this.updateRegistration.addClickListener(e -> {
            var cmd = CompleteOrUpdateStudentRegistrationByOrganizerCommand.apply(
                this.student.getId(), existingRegistration.getValue()
            );

            this.commandRunner.runAndNotify(cmd).ifPresent(i -> fireEvent(
                new StudentRegistrationUpdatedEvent(this, true, this.student)
            ));
        });

        this.setMargin(false);
        this.setPadding(false);
    }

    public void setStudent(Student student) {
        this.removeAll();

        if (Objects.isNull(initialRegistration)) {
            initialRegistration = new StudentRegistrationQuestionnaireControl(
                this.schoolTrip,
                student
            );
        }

        if (Objects.isNull(existingRegistration)) {
            existingRegistration = new StudentRegistrationQuestionnaireControl(
                this.schoolTrip,
                student
            );
        }

        this.student = student;
        initialRegistration.setSchoolTripAndStudent(schoolTrip, student);
        existingRegistration.setSchoolTripAndStudent(schoolTrip, student);

        if (student.getRegistrationState().equals(RegistrationState.CREATED)) {
            this.infoText.setText("Student is not registered yet. You may register the student manually.");
            this.add(infoText, initialRegistration, registerStudent);
        } else if (student.getRegistrationState().equals(RegistrationState.WAITING_FOR_CONFIRMATION)) {
            this.infoText.setText("Student has been registered, but confirmation link has not been called.");
            this.add(infoText, confirmRegistration);
        } else if (student.getRegistrationState().equals(RegistrationState.REGISTERED)) {
            this.infoText.setText("Student has been registred. You may change the configurations of the student.");
            this.add(infoText, existingRegistration, updateRegistration);
        }
    }

    public Registration addRegistrationUpdatedListener(
        ComponentEventListener<StudentRegistrationUpdatedEvent> listener) {

        return addListener(StudentRegistrationUpdatedEvent.class, listener);
    }

}
