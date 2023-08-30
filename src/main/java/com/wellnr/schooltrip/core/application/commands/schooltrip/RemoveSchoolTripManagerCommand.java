package com.wellnr.schooltrip.core.application.commands.schooltrip;

import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.user.RegisteredUserId;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RemoveSchoolTripManagerCommand implements AbstractSchoolTripCommand<MessageResult<Nothing>> {

    SchoolTripId schoolTrip;

    RegisteredUserId user;

    @Override
    public MessageResult<Nothing> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var trip = domainRegistry
            .getSchoolTrips()
            .getSchoolTripById(schoolTrip);

        trip.removeManager(user, this.user, domainRegistry.getSchoolTrips());

        return MessageResult.formatted(
            "Removed manager role from user."
        );
    }

}
