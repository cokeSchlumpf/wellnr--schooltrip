package com.wellnr.schooltrip.core.model.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DomainRole {

    private static final String NAME = "name";
    private static final String PERMISSIONS = "permissions";

    @JsonProperty(NAME)
    String name;

    @JsonProperty(PERMISSIONS)
    List<DomainPermission> permissions;

    @JsonCreator
    public static DomainRole apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(PERMISSIONS) List<DomainPermission> permissions) {

        return new DomainRole(name, permissions);
    }

}
