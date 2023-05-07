package com.wellnr.schooltrip.core.model.student;

import com.wellnr.ddd.DomainRepository;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;

import java.util.Optional;

public interface StudentsReadRepository extends DomainRepository {

    Optional<Student> findStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(
        SchoolTripId schoolTripId, String schoolClassName, String firstName, String lastName);

}
