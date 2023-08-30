package com.wellnr.schooltrip.core.model.user.rbac;

import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;
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
    @AllArgsConstructor(staticName = "apply")
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    public static class SchoolTripManager implements DomainRole {

        public static final String NAME = "/app/schooltrips/manager";

        String schoolTripId;


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
