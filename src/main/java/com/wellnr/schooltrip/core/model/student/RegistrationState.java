package com.wellnr.schooltrip.core.model.student;

public enum RegistrationState {

    /**
     * This state is the initial state, when the student is registered.
     */
    CREATED,

    /**
     * This state is set when registation is submitted but has not been completed
     * by email notification.
     */
    WAITING_FOR_CONFIRMATION,

    /**
     * The state as soon as the regsitration has been confirmed.
     */
    REGISTERED;

}
