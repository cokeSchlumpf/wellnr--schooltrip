package com.wellnr.schooltrip.core.model.student.questionaire;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Optional;

@Value
@AllArgsConstructor(staticName = "apply")
public class Snowboard implements Discipline {

    Experience experience;

    @Nullable
    SnowboardRental snowboardRental;

    @Nullable
    SnowboardBootRental snowboardBootRental;

    public Optional<SnowboardRental> getSnowboardRental() {
        return Optional.of(snowboardRental);
    }

    public Optional<SnowboardBootRental> getSnowboardBootRental() {
        return Optional.of(snowboardBootRental);
    }

}
