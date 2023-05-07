package com.wellnr.schooltrip.core.model.schooltrip;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wellnr.schooltrip.core.model.student.StudentId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Set;
import java.util.stream.Collectors;


@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SchoolClass {

    private static final String NAME = "name";
    private static final String STUDENTS = "students";

    @JsonProperty(NAME)
    String name;

    Set<StudentId> students;

    public static SchoolClass apply(String name) {
        return new SchoolClass(name, Set.of());
    }

    @JsonCreator
    public static SchoolClass apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(STUDENTS) Set<String> studentIds) {

        return new SchoolClass(
            name, studentIds.stream().map(StudentId::new).collect(Collectors.toSet())
        );
    }

    @JsonProperty(STUDENTS)
    public Set<String> getStudentIDs() {
        return students.stream().map(StudentId::id).collect(Collectors.toSet());
    }

}
