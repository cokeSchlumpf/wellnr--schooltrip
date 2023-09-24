package com.wellnr.schooltrip.core.model.student.exceptions;

import com.wellnr.ddd.DomainException;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;

public class StudentAlreadyExistsException extends DomainException {

    public StudentAlreadyExistsException(String summary) {
        super(summary);
    }

    public static StudentAlreadyExistsException apply() {
        return new StudentAlreadyExistsException("Student already exists.");
    }

    @Override
    public String getUserMessage(SchoolTripMessages i18n) {
        return i18n.studentAlreadyExists();
    }
}
