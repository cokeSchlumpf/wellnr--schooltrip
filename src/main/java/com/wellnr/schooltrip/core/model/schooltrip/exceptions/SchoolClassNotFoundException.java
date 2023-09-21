package com.wellnr.schooltrip.core.model.schooltrip.exceptions;

import com.wellnr.ddd.DomainException;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;

public class SchoolClassNotFoundException extends DomainException {

    private final String schoolClass;

    public SchoolClassNotFoundException(String schoolClass, String message) {
        super(message);
        this.schoolClass = schoolClass;
    }

    @Override
    public String getUserMessage(SchoolTripMessages i18n) {
        return i18n.schoolClassNotFound(schoolClass);
    }

    public static SchoolClassNotFoundException apply(String schoolClass) {
        return new SchoolClassNotFoundException(
            schoolClass,
            String.format("School class `%s` does not exist.", schoolClass)
        );
    }

}
