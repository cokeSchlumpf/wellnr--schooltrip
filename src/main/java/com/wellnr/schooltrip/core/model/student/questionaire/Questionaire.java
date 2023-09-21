package com.wellnr.schooltrip.core.model.student.questionaire;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Questionaire {

    Discipline disziplin;

    Nutrition nutrition;

    String comment;

    public static Questionaire empty() {
        return apply(
            Ski.apply(Experience.BEGINNER),
            Nutrition.apply(false, false),
            ""
        );
    }

}
