package com.wellnr.schooltrip.core.model.student.questionaire;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Questionnaire {

    Discipline disziplin;

    Nutrition nutrition;

    String comment;

    TShirtSelection tShirtSelection;

    public static Questionnaire empty() {
        return apply(
            Ski.apply(Experience.BEGINNER),
            Nutrition.apply(false, false),
            "",
            TShirtSelection.NONE
        );
    }

}
