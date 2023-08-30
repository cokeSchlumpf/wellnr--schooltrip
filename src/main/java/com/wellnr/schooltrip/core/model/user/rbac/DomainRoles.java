package com.wellnr.schooltrip.core.model.user.rbac;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Set;

public final class DomainRoles {

    private DomainRoles() {

    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class ApplicationAdministrator implements DomainRole {

        public static final String NAME = "/app/admin";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public String getDisplayName(SchoolTripDomainRegistry domainRegistry) {
            return NAME;
        }

        @Override
        public Set<DomainPermission> getPermissions() {
            return Set.of(
                DomainPermissions.ManageApplication.apply(),
                DomainPermissions.ManageSchoolTrips.apply()
            );
        }
    }

    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SchoolTripManager implements DomainRole {

        private static final String SCHOOL_TRIP_ID = "school-trip-id";

        public static final String NAME = "/app/schooltrips/manager";

        @JsonProperty(SCHOOL_TRIP_ID)
        String schoolTripId;

        @JsonCreator
        public static SchoolTripManager apply(
            @JsonProperty(SCHOOL_TRIP_ID) String schoolTripId
        ) {
            return new SchoolTripManager(schoolTripId);
        }


        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public String getDisplayName(SchoolTripDomainRegistry domainRegistry) {
            return domainRegistry
                .getSchoolTrips()
                .findSchoolTripById(schoolTripId)
                .map(trip -> {
                   return "/app/schooltrips/" + trip.getName() + "/manager";
                })
                .orElse("/app/schooltrips/" + schoolTripId + "/manager");
        }

        @Override
        public Set<DomainPermission> getPermissions() {
            return Set.of(
                DomainPermissions.ManageSchoolTrip.apply(this.schoolTripId)
            );
        }

    }

}
