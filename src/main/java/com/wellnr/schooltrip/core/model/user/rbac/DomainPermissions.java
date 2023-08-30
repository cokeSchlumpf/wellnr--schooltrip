package com.wellnr.schooltrip.core.model.user.rbac;

import lombok.AllArgsConstructor;
import lombok.Value;

public class DomainPermissions {

    private DomainPermissions() {

    }

    public interface SchoolTripPermission extends DomainPermission {

        String getSchooltripId();

    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class ManageSchoolTrips implements DomainPermission {
        public static final String NAME = "/app/schooltrips/manage";
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class ManageApplication implements DomainPermission {
        public static final String NAME = "/app/admin/manage";
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class ManageSchoolTrip implements SchoolTripPermission {

        public static final String NAME = "/app/schooltrips/manage-trip";

        String schooltripId;

    }

}
