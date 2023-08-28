package com.wellnr.schooltrip.ui.components.student;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.shared.Registration;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;

public class StudentDetailsControl extends Scroller {

    private final String appBaseUrl;

    private StudentBasicsControl basics;

    private StudentPaymentAdminControl payments;

    private StudentRegistrationAdminControl registration;

    private Student student;

    public StudentDetailsControl(String appBaseUrl, SchoolTrip schoolTrip, SchoolTripCommandRunner commandRunner) {
        this.appBaseUrl = appBaseUrl;

        setWidth("800px");
        setMaxWidth("800");
        setVisible(false);

        // Setup child controls
        setupBasics(schoolTrip, commandRunner);
        setupPayment(commandRunner);
        setupRegistration(schoolTrip, commandRunner);

        // Setup layout
        var tabs = new TabSheet();
        tabs.add(new Tab("Student"), basics);
        tabs.add(new Tab("Registration"), this.registration);
        tabs.add(new Tab("Payments"), this.payments);

        var vl = new VerticalLayout(tabs);
        this.setContent(vl);
    }

    /**
     * Add a listener which is triggered when student details get updated by
     * the control (the update is already confirmed by the backend when the
     * event is fired).
     *
     * @param listener The listener for the event.
     * @return An event registration.
     */
    public Registration addStudentDetailsUpdatedListener(
        ComponentEventListener<StudentDetailsUpdatedEvent> listener) {

        return addListener(StudentDetailsUpdatedEvent.class, listener);
    }

    /**
     * Hide the control.
     */
    public void close() {
        this.setVisible(false);
    }

    /**
     * Set the current student in the control.
     *
     * @param student The student.
     */
    public void setStudent(Student student) {
        this.student = student;
        this.setVisible(true);

        this.basics.setStudent(student);
        this.payments.setStudent(student);
        this.registration.setStudent(student);
    }

    /**
     * Sets up the controls for editing basic student information.
     */
    private void setupBasics(SchoolTrip schoolTrip, SchoolTripCommandRunner commandRunner) {
        this.basics = new StudentBasicsControl(appBaseUrl, schoolTrip, commandRunner);

        this.basics.addBasicsUpdatedListener(event -> fireEvent(
            new StudentDetailsUpdatedEvent(student, this, true)
        ));
    }

    /**
     * Sets up controls for managing payments.
     *
     * @param commandRunner The command runner to execute commands.
     */
    private void setupPayment(SchoolTripCommandRunner commandRunner) {
        this.payments = new StudentPaymentAdminControl(commandRunner);

        this.payments.addPaymentUpdatedListener(
            event -> fireEvent(new StudentDetailsUpdatedEvent(student, this, true))
        );
    }

    /**
     * Sets up the student registration form.
     *
     * @param schoolTrip The current school trip.
     * @param commandRunner The runner to execute the command.
     */
    private void setupRegistration(SchoolTrip schoolTrip, SchoolTripCommandRunner commandRunner) {
        this.registration = new StudentRegistrationAdminControl(
            schoolTrip, commandRunner
        );

        this.registration.addRegistrationUpdatedListener(event -> {
            fireEvent(new StudentDetailsUpdatedEvent(event.getStudent(), this, true));
        });
    }

}