package com.wellnr.schooltrip.core.model.schooltrip.exceptions;

import com.wellnr.ddd.DomainException;

public class SchoolClassAlreadyExistsException extends DomainException {
    public SchoolClassAlreadyExistsException(String message) {
        super(message);
    }

    public SchoolClassAlreadyExistsException apply() {
        return new SchoolClassAlreadyExistsException("School class already exists.");
    }

}
