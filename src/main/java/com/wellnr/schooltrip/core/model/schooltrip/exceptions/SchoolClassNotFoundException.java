package com.wellnr.schooltrip.core.model.schooltrip.exceptions;

import com.wellnr.ddd.DomainException;

public class SchoolClassNotFoundException extends DomainException {
    public SchoolClassNotFoundException(String message) {
        super(message);
    }

    public static SchoolClassNotFoundException apply(String schoolClass) {
        return new SchoolClassNotFoundException(
            String.format("School class `%s` does not exist.", schoolClass)
        );
    }

}
