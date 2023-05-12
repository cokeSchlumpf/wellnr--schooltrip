package com.wellnr.schooltrip.core.model.student.questionaire;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class Questionaire {

    Discipline disziplin;

    Nutrition nutrition;

    String comment;

}
