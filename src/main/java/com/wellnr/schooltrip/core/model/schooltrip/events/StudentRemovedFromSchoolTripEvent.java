package com.wellnr.schooltrip.core.model.schooltrip.events;

import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.student.StudentId;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * This event is fired when a student has been removed from a school trip.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class StudentRemovedFromSchoolTripEvent {

    SchoolTripId schoolTrip;

    StudentId student;

}
