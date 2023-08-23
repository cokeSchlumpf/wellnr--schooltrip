package com.wellnr.schooltrip.core.model.student.questionaire;

public sealed interface Rental permits SkiRental, SnowboardRental {

    int getHeight();

    int getWeight();

}
