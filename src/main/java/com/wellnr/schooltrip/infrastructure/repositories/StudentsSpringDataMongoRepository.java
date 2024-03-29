package com.wellnr.schooltrip.infrastructure.repositories;

import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.student.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StudentsSpringDataMongoRepository extends MongoRepository<Student, String> {

    Optional<Student> findStudentByConfirmationToken(String token);

    Optional<Student> findStudentById(String id);

    Optional<Student> findStudentByPaymentToken(String paymentToken);

    Optional<Student> findStudentBySchoolTripAndSchoolClassAndFirstNameAndLastName(
        SchoolTripId schoolTripId, String schoolClassName, String firstName, String lastName
    );

    Optional<Student> findStudentByToken(String token);

    List<Student> findStudentsBySchoolTrip(SchoolTripId schoolTripId);

    List<Student> findStudentsBySchoolTripAndSchoolClass(SchoolTripId schoolTripId, String schoolClassName);

}
