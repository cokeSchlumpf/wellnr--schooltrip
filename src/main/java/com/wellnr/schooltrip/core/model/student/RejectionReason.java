package com.wellnr.schooltrip.core.model.student;

public enum RejectionReason {

    /**
     * This state is the initial state, when the student is registered.
     */
    OUT_OF_SNOW,

    /**
     * Student has confirmed to attend Out of Snow, or go to school.
     */
    GO_TO_SCHOOL;

}
