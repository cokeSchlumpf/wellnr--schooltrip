package com.wellnr.schooltrip.core.model.user.rbac;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

public record DomainPermission(@JsonProperty(NAME) String domainPermission, @JsonIgnore URI subject) {

    private static final String NAME = "name";

    @JsonCreator
    public DomainPermission(
        @JsonProperty(NAME) String domainPermission) {

        this(domainPermission, URI.create("urn:app"));
    }

    public DomainPermission onSubject(URI subject) {
        return new DomainPermission(domainPermission, subject);
    }

}
