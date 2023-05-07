package com.wellnr.schooltrip.core.application.commands;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.CommandResult;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RegisterSchoolClassCommand implements DomainCommand {

    String schoolTrip;

    String name;

    @Override
    public MessageResult<Nothing> run(User user, SchoolTripDomainRegistry domainRegistry) {
        domainRegistry
            .getSchoolTrips()
            .getSchoolTripByName(schoolTrip)
            .registerSchoolClass(user, name, domainRegistry.getSchoolTrips());

        return MessageResult.formatted(
            "Successfully created school `%s` class.", name
        );
    }

}
