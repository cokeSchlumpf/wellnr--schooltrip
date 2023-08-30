package com.wellnr.schooltrip.core.model.user.rbac;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(
        value = DomainPermissions.ManageApplication.class,
        name = DomainPermissions.ManageApplication.NAME
    ),
    @JsonSubTypes.Type(
        value = DomainPermissions.ManageSchoolTrips.class,
        name = DomainPermissions.ManageSchoolTrips.NAME
    ),
    @JsonSubTypes.Type(
        value = DomainPermissions.ManageSchoolTrip.class,
        name = DomainPermissions.ManageSchoolTrip.NAME
    )
})
public interface DomainPermission {
}
