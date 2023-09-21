package com.wellnr.schooltrip.core.model.schooltrip.exceptions;

import com.wellnr.ddd.DomainException;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;

public class SchoolClassAlreadyExistsException extends DomainException {
    public SchoolClassAlreadyExistsException(String message) {
        super(message);
    }

    @Override
    public String getUserMessage(SchoolTripMessages i18n) {
        return i18n.schoolClassAlreadyExists();
    }

    public SchoolClassAlreadyExistsException apply() {
        return new SchoolClassAlreadyExistsException("School class already exists.");
    }

}
