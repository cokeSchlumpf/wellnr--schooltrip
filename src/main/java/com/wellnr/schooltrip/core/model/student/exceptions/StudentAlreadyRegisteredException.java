package com.wellnr.schooltrip.core.model.student.exceptions;

import com.wellnr.ddd.DomainException;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;

public class StudentAlreadyRegisteredException extends DomainException {

    private final Student student;

    public StudentAlreadyRegisteredException(String message, Student student) {
        super(message);
        this.student = student;
    }

    public static StudentAlreadyRegisteredException apply(Student student) {
        return new StudentAlreadyRegisteredException(
            "%s is already registered.".formatted(student.getFirstName()),
            student
        );
    }

    @Override
    public String getUserMessage(SchoolTripMessages i18n) {
        return i18n.studentIsAlreadyRegistered(student);
    }

}
