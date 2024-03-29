package com.wellnr.schooltrip.infrastructure.repositories;

import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.StudentsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor(staticName = "apply")
public class StudentsMongoRepository implements StudentsRepository {

    private final StudentsSpringDataMongoRepository spring;

    @Override
    public Optional<Student> findStudentByConfirmationToken(String token) {
        return spring.findStudentByConfirmationToken(token);
    }

    @Override
    public Optional<Student> findStudentById(String id) {
        return spring.findStudentById(id);
    }

    @Override
    public Optional<Student> findStudentByPaymentToken(String paymentToken) {
        return spring.findStudentByPaymentToken(paymentToken);
    }

    @Override
    public Optional<Student> findStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(SchoolTripId schoolTripId, String schoolClassName, String firstName, String lastName) {
        return spring.findStudentBySchoolTripAndSchoolClassAndFirstNameAndLastName(schoolTripId, schoolClassName,
            firstName, lastName);
    }

    @Override
    public Optional<Student> findStudentByToken(String token) {
        return spring.findStudentByToken(token);
    }

    @Override
    public List<Student> findStudentsBySchoolTrip(SchoolTripId schoolTripId) {
        return spring.findStudentsBySchoolTrip(schoolTripId);
    }

    @Override
    public List<Student> findStudentsBySchoolTripAndSchoolClassName(SchoolTripId schoolTripId, String schoolClassName) {
        return spring.findStudentsBySchoolTripAndSchoolClass(schoolTripId, schoolClassName);
    }

    @Override
    public void insertOrUpdateStudent(Student student) {
        spring.save(student);
    }

    @Override
    public void remove(Student student) {
        spring.delete(student);
    }

}
