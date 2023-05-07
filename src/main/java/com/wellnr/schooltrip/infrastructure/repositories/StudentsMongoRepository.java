package com.wellnr.schooltrip.infrastructure.repositories;

import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.StudentsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor(staticName = "apply")
public class StudentsMongoRepository implements StudentsRepository {

    private final StudentsSpringDataMongoRepository spring;

    @Override
    public Optional<Student> findStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(SchoolTripId schoolTripId, String schoolClassName, String firstName, String lastName) {
        return spring.findStudentBySchoolTripAndSchoolClassAndFirstNameAndLastName(schoolTripId, schoolClassName, firstName, lastName);
    }

    @Override
    public void insertOrUpdateStudent(Student student) {
        spring.save(student);
    }

}
