package com.wellnr.ddd.commands;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wellnr.schooltrip.core.model.user.User;


@JsonIgnoreProperties({"command"})
public interface Command<DOMAIN_REGISTRY_TYPE> {

    Class<DOMAIN_REGISTRY_TYPE> getDomainRegistryType();

    CommandResult run(User user, DOMAIN_REGISTRY_TYPE domainRegistry);

}
