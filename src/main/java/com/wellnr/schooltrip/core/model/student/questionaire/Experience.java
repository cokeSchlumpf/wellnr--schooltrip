package com.wellnr.schooltrip.core.model.student.questionaire;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Experience {

    BEGINNER("beginner"),
    INTERMEDIATE("intermediate"),
    EXPERT("expert");

    @JsonValue()
    private final String value;

    Experience(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
