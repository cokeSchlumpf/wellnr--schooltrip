package com.wellnr.schooltrip.ui.components.student;

import com.vaadin.flow.component.ComponentEvent;

public class StudentPaymentsUpdatedEvent extends ComponentEvent<StudentPaymentAdminControl> {

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param source     the source component
     * @param fromClient <code>true</code> if the event originated from the client
     *                   side, <code>false</code> otherwise
     */
    public StudentPaymentsUpdatedEvent(StudentPaymentAdminControl source, boolean fromClient) {
        super(source, fromClient);
    }

}
