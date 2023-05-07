package com.wellnr.schooltrip.core.model.student;

import com.wellnr.ddd.DomainException;

public class StudentAlreadyExistsException extends DomainException {

    public StudentAlreadyExistsException(String summary) {
        super(summary);
    }

    public static StudentAlreadyExistsException apply() {
        return new StudentAlreadyExistsException("Student already exists.");
    }
}
