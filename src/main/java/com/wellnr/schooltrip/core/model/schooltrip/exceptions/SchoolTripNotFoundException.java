package com.wellnr.schooltrip.core.model.schooltrip.exceptions;

import com.wellnr.ddd.DomainException;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;

import java.text.MessageFormat;

public class SchoolTripNotFoundException extends DomainException {

    private final String name;

    private SchoolTripNotFoundException(String name, String message) {
        super(message);
        this.name = name;
    }

    public static SchoolTripNotFoundException withName(String name) {
        var message = MessageFormat.format(
            "Schooltrip `{0}` not found.",
            name
        );

        return new SchoolTripNotFoundException(name, message);
    }

    @Override
    public String getUserMessage(SchoolTripMessages i18n) {
        return i18n.schoolTripNotFound(name);
    }

}
