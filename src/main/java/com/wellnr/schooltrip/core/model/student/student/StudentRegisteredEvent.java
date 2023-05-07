package com.wellnr.schooltrip.core.model.student.student;

import com.wellnr.schooltrip.core.model.student.Student;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class StudentRegisteredEvent {

    Student student;

}
