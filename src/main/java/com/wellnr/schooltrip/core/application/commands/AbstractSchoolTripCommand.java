package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.ddd.commands.Command;
import com.wellnr.ddd.commands.CommandResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.user.User;

public interface AbstractSchoolTripCommand<T extends CommandResult> extends Command<SchoolTripDomainRegistry> {

    @Override
    default Class<SchoolTripDomainRegistry> getDomainRegistryType() {
        return SchoolTripDomainRegistry.class;
    }

    @Override
    T run(User user, SchoolTripDomainRegistry domainRegistry);

}
