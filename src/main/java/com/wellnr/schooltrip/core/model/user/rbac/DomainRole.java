package com.wellnr.schooltrip.core.model.user.rbac;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;

import java.util.Set;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(
        value = DomainRoles.ApplicationAdministrator.class,
        name = DomainRoles.ApplicationAdministrator.NAME
    ),
    @JsonSubTypes.Type(
        value = DomainRoles.SchoolTripManager.class,
        name = DomainRoles.SchoolTripManager.NAME
    )
})
public interface DomainRole {

    @JsonIgnore
    String getName();

    @JsonIgnore
    String getDisplayName(SchoolTripDomainRegistry domainRegistry);

    @JsonIgnore
    Set<DomainPermission> getPermissions();

}
