package com.wellnr.schooltrip.core.model.student.questionaire;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Optional;

@Value
@AllArgsConstructor(staticName = "apply")
public class Ski implements Discipline {

    Experience experience;

    SkiRental skiRental;

    SkiBootRental skiBootRental;

    public Optional<SkiBootRental> getSkiBootRental() {
        return Optional.of(skiBootRental);
    }

    public Optional<SkiRental> getSkiRental() {
        return Optional.of(skiRental);
    }

}
