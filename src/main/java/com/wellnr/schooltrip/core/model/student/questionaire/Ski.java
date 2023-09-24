package com.wellnr.schooltrip.core.model.student.questionaire;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.Optional;

@Value
@AllArgsConstructor(staticName = "apply")
public class Ski implements Discipline {

    Experience experience;

    @With
    SkiRental skiRental;

    @With
    SkiBootRental skiBootRental;

    @With
    boolean helmRental;

    public static Ski apply(Experience experience) {
        return apply(experience, null, null, false);
    }

    public Optional<SkiBootRental> getBootRental() {
        return Optional.ofNullable(skiBootRental);
    }

    public Optional<SkiRental> getRental() {
        return Optional.ofNullable(skiRental);
    }

    @Override
    public boolean hasHelmRental() {
        return helmRental;
    }

}
