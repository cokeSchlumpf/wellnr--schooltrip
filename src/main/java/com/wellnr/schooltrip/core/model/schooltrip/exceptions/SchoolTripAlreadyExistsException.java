package com.wellnr.schooltrip.core.model.schooltrip.exceptions;

import com.wellnr.ddd.DomainException;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;

import java.text.MessageFormat;

public class SchoolTripAlreadyExistsException extends DomainException {

    private final String name;

    private SchoolTripAlreadyExistsException(String name, String message) {
        super(message);

        this.name = name;
    }

    @Override
    public String getUserMessage(SchoolTripMessages i18n) {
        return i18n.schoolTripAlreadyExists(name);
    }

    public static SchoolTripAlreadyExistsException apply(String name) {
        var message = MessageFormat.format(
            "School trip `{0}` already exists.",
            name
        );

        return new SchoolTripAlreadyExistsException(name, message);
    }



}
