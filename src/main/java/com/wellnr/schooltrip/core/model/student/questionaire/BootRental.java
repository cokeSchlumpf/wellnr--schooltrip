package com.wellnr.schooltrip.core.model.student.questionaire;

public sealed interface BootRental permits SkiBootRental, SnowboardBootRental {

    int getSize();

}
