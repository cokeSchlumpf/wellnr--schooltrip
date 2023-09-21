package com.wellnr.schooltrip.core.model.student;

import com.wellnr.ddd.DomainRepository;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.student.exceptions.StudentNotFoundExcepption;
import com.wellnr.schooltrip.core.model.student.exceptions.TokenNotFoundException;

import java.util.List;
import java.util.Optional;

public interface StudentsReadRepository extends DomainRepository {

    Optional<Student> findStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(
        SchoolTripId schoolTripId, String schoolClassName, String firstName, String lastName);

    default Student getStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(
        SchoolTripId schoolTripId, String schoolClassName, String firstName, String lastName) {

        return this
            .findStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(schoolTripId, schoolClassName, firstName, lastName)
            .orElseThrow(
                () -> StudentNotFoundExcepption.apply(schoolClassName, firstName, lastName)
            );
    }

    List<Student> findStudentsBySchoolTrip(
        SchoolTripId schoolTripId
    );

    List<Student> findStudentsBySchoolTripAndSchoolClassName(
        SchoolTripId schoolTripId, String schoolClassName
    );

    Optional<Student> findStudentById(String id);

    default Student getStudentById(String id) {
        return findStudentById(id).orElseThrow();
    }

    Optional<Student> findStudentByToken(String token);

    default Student getStudentByToken(String token) {
        return findStudentByToken(token).orElseThrow(
            () -> TokenNotFoundException.apply(token)
        );
    }

    Optional<Student> findStudentByConfirmationToken(String token);

    default Student getStudentByConfirmationToken(String token) {
        return findStudentByConfirmationToken(token).orElseThrow(
            () -> TokenNotFoundException.apply(token)
        );
    }

}
