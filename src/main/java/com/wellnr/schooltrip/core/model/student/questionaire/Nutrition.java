package com.wellnr.schooltrip.core.model.student.questionaire;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class Nutrition {

    boolean vegetarian;

    boolean halal;

    public static Nutrition apply() {
        return apply(false, false);
    }
}
