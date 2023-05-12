package com.wellnr.schooltrip.core.model.student.questionaire;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.Optional;

@Value
@AllArgsConstructor(staticName = "apply")
public class Snowboard implements Discipline {

    Experience experience;

    @With
    @Nullable
    SnowboardRental snowboardRental;

    @With
    @Nullable
    SnowboardBootRental snowboardBootRental;

    public static Snowboard apply(Experience experience) {
        return apply(experience, null, null);
    }

    public Optional<SnowboardRental> getSnowboardRental() {
        return Optional.of(snowboardRental);
    }

    public Optional<SnowboardBootRental> getSnowboardBootRental() {
        return Optional.of(snowboardBootRental);
    }

}
