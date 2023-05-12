package com.wellnr.schooltrip.core.model.student;

import com.wellnr.ddd.DomainRepository;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;

import java.util.List;
import java.util.Optional;

public interface StudentsReadRepository extends DomainRepository {

    Optional<Student> findStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(
        SchoolTripId schoolTripId, String schoolClassName, String firstName, String lastName);

    default Student getStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(
        SchoolTripId schoolTripId, String schoolClassName, String firstName, String lastName) {
        return this.findStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(schoolTripId, schoolClassName, firstName, lastName)
            .orElseThrow();
    }

    List<Student> findStudentsBySchoolTrip(
        SchoolTripId schoolTripId
    );

    Optional<Student> findStudentByToken(String token);

    default Student getStudentByToken(String token) {
        return findStudentByToken(token).orElseThrow();
    }

}
