package com.wellnr.schooltrip.core.model.schooltrip.exceptions;

import com.wellnr.ddd.DomainException;

import java.text.MessageFormat;

public class SchoolTripNotFoundException extends DomainException {

    private SchoolTripNotFoundException(String message) {
        super(message);
    }

    public static SchoolTripNotFoundException withName(String name) {
        var message = MessageFormat.format(
            "Schooltrip `{0}` not found.",
            name
        );

        return new SchoolTripNotFoundException(message);
    }

}
