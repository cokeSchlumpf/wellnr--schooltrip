package com.wellnr.schooltrip.core.model.student.questionaire;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class SnowboardRental implements Rental {

    /**
     * Student height in centimeters.
     */
    int height;

    /**
     * Student Weight in Kilogram.
     */
    int weight;

}
