package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.ddd.commands.CommandResult;
import com.wellnr.ddd.commands.DataResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.user.User;

public class ListSchoolTripsCommand implements DomainCommand {

    @Override
    public CommandResult run(User user, SchoolTripDomainRegistry domainRegistry) {
        return DataResult.apply(
            domainRegistry.getSchoolTrips().findAllSchoolTrips().stream().toList()
        );
    }

}
