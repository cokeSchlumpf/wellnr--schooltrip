package com.wellnr.schooltrip.infrastructure.repositories;

import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.student.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StudentsSpringDataMongoRepository extends MongoRepository<Student, String> {

    Optional<Student> findStudentBySchoolTripAndSchoolClassAndFirstNameAndLastName(SchoolTripId schoolTripId, String schoolClassName, String firstName, String lastName);

    List<Student> findStudentsBySchoolTrip(SchoolTripId schoolTripId);

    Optional<Student> findStudentByToken(String token);

    Optional<Student> findStudentByConfirmationToken(String token);

}
