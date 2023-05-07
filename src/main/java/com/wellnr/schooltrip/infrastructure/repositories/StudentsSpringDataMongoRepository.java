package com.wellnr.schooltrip.infrastructure.repositories;

import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.student.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StudentsSpringDataMongoRepository extends MongoRepository<Student, String> {

    Optional<Student> findStudentBySchoolTripAndSchoolClassAndFirstNameAndLastName(SchoolTripId schoolTripId, String schoolClassName, String firstName, String lastName);

}
