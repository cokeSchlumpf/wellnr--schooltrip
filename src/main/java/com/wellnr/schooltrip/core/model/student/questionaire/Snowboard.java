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

    @With
    boolean helmRental;

    public static Snowboard apply(Experience experience) {
        return apply(experience, null, null, false);
    }

    public Optional<SnowboardBootRental> getBootRental() {
        return Optional.ofNullable(snowboardBootRental);
    }

    public Optional<SnowboardRental> getRental() {
        return Optional.ofNullable(snowboardRental);
    }

    public boolean hasHelmRental() {
        return helmRental;
    }

}
