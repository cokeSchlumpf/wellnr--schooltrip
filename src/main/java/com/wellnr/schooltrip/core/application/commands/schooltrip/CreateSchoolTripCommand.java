package com.wellnr.schooltrip.core.application.commands.schooltrip;

import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateSchoolTripCommand implements AbstractSchoolTripCommand<MessageResult<SchoolTrip>> {

    @NotBlank
    @Size(min = 3, max = 256)
    String title;

    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9-]+")
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
