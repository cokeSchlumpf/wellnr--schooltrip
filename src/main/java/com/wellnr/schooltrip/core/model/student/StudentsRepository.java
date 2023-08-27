package com.wellnr.schooltrip.core.model.student;

public interface StudentsRepository extends StudentsReadRepository {

    void insertOrUpdateStudent(Student student);

    void remove(Student student);

}
