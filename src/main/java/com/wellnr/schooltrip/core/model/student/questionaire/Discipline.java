package com.wellnr.schooltrip.core.model.student.questionaire;

import java.util.Optional;

public sealed interface Discipline permits Ski, Snowboard {

    Experience getExperience();

    Optional<? extends Rental> getRental();

    Optional<? extends BootRental> getBootRental();

}
