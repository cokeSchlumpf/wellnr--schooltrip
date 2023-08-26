package com.wellnr.schooltrip.ui.components.student;

import com.vaadin.flow.component.ComponentEvent;
import com.wellnr.schooltrip.core.model.student.Student;

public class StudentBasicsUpdatedEvent extends ComponentEvent<StudentBasicsControl> {

    Student student;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param student    the student which got updated
     * @param source     the source component
     * @param fromClient <code>true</code> if the event originated from the client
     *                   side, <code>false</code> otherwise
     */
    public StudentBasicsUpdatedEvent(
        StudentBasicsControl source, boolean fromClient, Student student
    ) {

        super(source, fromClient);
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }
}
