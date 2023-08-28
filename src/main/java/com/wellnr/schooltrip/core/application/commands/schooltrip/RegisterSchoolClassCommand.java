package com.wellnr.schooltrip.core.application.commands.schooltrip;

import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.*;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RegisterSchoolClassCommand implements AbstractSchoolTripCommand<MessageResult<Nothing>> {

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
