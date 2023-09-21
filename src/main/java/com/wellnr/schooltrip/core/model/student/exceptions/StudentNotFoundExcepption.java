package com.wellnr.schooltrip.core.model.student.exceptions;

import com.wellnr.ddd.DomainException;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;

public class StudentNotFoundExcepption extends DomainException {

    public StudentNotFoundExcepption(String summary) {
        super(summary);
    }

    @Override
    public String getUserMessage(SchoolTripMessages i18n) {
        return getMessage();
    }

    public static StudentNotFoundExcepption apply(String schoolClassName, String firstName, String lastName) {
        var msg = String.format("Student with %s %s not found in class %s.", firstName, lastName, schoolClassName);
        return new StudentNotFoundExcepption(msg);
    }

}
