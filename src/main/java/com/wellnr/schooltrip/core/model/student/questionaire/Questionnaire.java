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

    boolean cityTripAllowance;

    public static Questionnaire empty() {
        return apply(
            Ski.apply(Experience.BEGINNER),
            Nutrition.apply(false, false),
            "",
            TShirtSelection.NONE,
            false
        );
    }

    public static Questionnaire fakeSki() {
        return apply(
            Ski.apply(Experience.BEGINNER)
                .withHelmRental(true)
                .withSkiRental(SkiRental.apply(162, 42))
                .withSkiBootRental(SkiBootRental.apply(37)),
            Nutrition.apply(false, false),
            "",
            TShirtSelection.NONE,
            false
        );
    }

    public static Questionnaire fakeSnowboard() {
        return apply(
            Snowboard
                .apply(Experience.BEGINNER)
                .withHelmRental(true)
                .withSnowboardRental(SnowboardRental.apply(162, 42))
                .withSnowboardBootRental(SnowboardBootRental.apply(37)),
            Nutrition.apply(false, true),
            "",
            TShirtSelection.NONE,
            false
        );
    }

}
