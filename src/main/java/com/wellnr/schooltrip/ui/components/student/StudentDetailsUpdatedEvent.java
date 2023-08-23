package com.wellnr.schooltrip.ui.components.student;

import com.vaadin.flow.component.ComponentEvent;
import com.wellnr.schooltrip.core.model.student.Student;

public class StudentDetailsUpdatedEvent extends ComponentEvent<StudentDetailsControl> {

    Student student;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param student    the student which has been updated.
     * @param source     the source component
     * @param fromClient <code>true</code> if the event originated from the client
     *                   side, <code>false</code> otherwise
     */
    public StudentDetailsUpdatedEvent(Student student, StudentDetailsControl source, boolean fromClient) {
        super(source, fromClient);
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }

}
