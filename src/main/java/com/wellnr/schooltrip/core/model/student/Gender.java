package com.wellnr.schooltrip.core.model.student;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {

    Male("male"),
    Female("female"),
    NotSpecified("not specified");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
