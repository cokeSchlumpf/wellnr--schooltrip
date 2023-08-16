package com.wellnr.schooltrip.core.model.student.payments;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentType {

    ONLINE("online"), CASH("cash"), TRANSACTION("transaction");

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
