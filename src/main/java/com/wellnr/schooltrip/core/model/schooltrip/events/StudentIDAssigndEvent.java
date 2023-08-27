package com.wellnr.schooltrip.core.model.schooltrip.events;

import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.student.StudentId;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * This event is fired when a school trip assigns a new (ordered, increasing) ID to a student.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class StudentIDAssigndEvent {

    SchoolTripId schoolTrip;

    StudentId student;

    Integer id;

}
