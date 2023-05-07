package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.ddd.commands.Command;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;

public interface DomainCommand extends Command<SchoolTripDomainRegistry> {

    @Override
    default Class<SchoolTripDomainRegistry> getDomainRegistryType() {
        return SchoolTripDomainRegistry.class;
    }

}
