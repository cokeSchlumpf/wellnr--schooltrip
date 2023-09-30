package com.wellnr.schooltrip.core.model.student;

import com.wellnr.ddd.DomainRepository;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.student.exceptions.StudentNotFoundExcepption;
import com.wellnr.schooltrip.core.model.student.exceptions.TokenNotFoundException;

import java.util.List;
import java.util.Optional;

public interface StudentsReadRepository extends DomainRepository {

    Optional<Student> findStudentByConfirmationToken(String token);

    Optional<Student> findStudentById(String id);

    Optional<Student> findStudentByPaymentToken(String paymentToken);

    Optional<Student> findStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(
        SchoolTripId schoolTripId, String schoolClassName, String firstName, String lastName);

    Optional<Student> findStudentByToken(String token);

    List<Student> findStudentsBySchoolTrip(
        SchoolTripId schoolTripId
    );

    List<Student> findStudentsBySchoolTripAndSchoolClassName(
        SchoolTripId schoolTripId, String schoolClassName
    );

    default Student getStudentByConfirmationToken(String token) {
        return findStudentByConfirmationToken(token).orElseThrow(
            () -> TokenNotFoundException.apply(token)
        );
    }

    default Student getStudentById(String id) {
        return findStudentById(id).orElseThrow();
    }

    default Student getStudentByPaymentToken(String paymentToken) {
        return findStudentByPaymentToken(paymentToken).orElseThrow(
            () -> TokenNotFoundException.apply(paymentToken)
        );
    }

    default Student getStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(
        SchoolTripId schoolTripId, String schoolClassName, String firstName, String lastName) {

        return this
            .findStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(schoolTripId, schoolClassName,
                firstName, lastName)
            .orElseThrow(
                () -> StudentNotFoundExcepption.apply(schoolClassName, firstName, lastName)
            );
    }

    default Student getStudentByToken(String token) {
        return findStudentByToken(token).orElseThrow(
            () -> TokenNotFoundException.apply(token)
        );
    }

}
