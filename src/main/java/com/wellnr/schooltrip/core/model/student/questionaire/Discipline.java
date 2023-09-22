package com.wellnr.schooltrip.core.model.student.questionaire;

import java.util.Optional;

public sealed interface Discipline permits Ski, Snowboard {

    Optional<? extends BootRental> getBootRental();

    Experience getExperience();

    Optional<? extends Rental> getRental();

    boolean hasHelmRental();

}
