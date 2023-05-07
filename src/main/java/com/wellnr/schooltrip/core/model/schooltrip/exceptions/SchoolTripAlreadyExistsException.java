package com.wellnr.schooltrip.core.model.schooltrip.exceptions;

import com.wellnr.ddd.DomainException;

import java.text.MessageFormat;

public class SchoolTripAlreadyExistsException extends DomainException {

    private SchoolTripAlreadyExistsException(String message) {
        super(message);
    }

    public static SchoolTripAlreadyExistsException apply(String name) {
        var message = MessageFormat.format(
            "School trip `{0}` already exists.",
            name
        );

        return new SchoolTripAlreadyExistsException(message);
    }

}
