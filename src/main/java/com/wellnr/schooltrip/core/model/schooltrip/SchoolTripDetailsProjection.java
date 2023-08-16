package com.wellnr.schooltrip.core.model.schooltrip;

import com.wellnr.schooltrip.core.model.student.Student;

import java.util.List;

public record SchoolTripDetailsProjection(
    SchoolTrip schoolTrip,
    List<Student> students
) {

}
