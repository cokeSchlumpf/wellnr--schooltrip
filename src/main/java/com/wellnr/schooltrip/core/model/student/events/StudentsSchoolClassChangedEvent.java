package com.wellnr.schooltrip.core.model.student.events;

import com.wellnr.schooltrip.core.model.student.Student;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class StudentsSchoolClassChangedEvent {

    String oldClass;

    Student student;

}
