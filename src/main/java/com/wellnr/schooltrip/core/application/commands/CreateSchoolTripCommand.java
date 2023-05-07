package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateSchoolTripCommand implements DomainCommand {

    @NotBlank
    @Size(min = 3, max = 256)
    String title;

    @NotBlank
    String name;

    public static CreateSchoolTripCommand apply(String title) {
        return apply(title, null);
    }

    @Override
    public MessageResult<SchoolTrip> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var trip = SchoolTrip.create(title, name);

        trip.create(
            user, domainRegistry.getSchoolTrips(), domainRegistry.getValidation()
        );

        return MessageResult
            .formatted("Successfully created trip `%s`", title)
            .withData(trip);
    }

}
