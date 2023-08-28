package com.wellnr.schooltrip.core.model.user.rbac;

public class DomainPermissions {

    private DomainPermissions() {

    }

    // Application wide permissions (whole Domain)
    public static final DomainPermission APPLICATION__MANAGE_TRIPS = new DomainPermission("/app/manage-trips");

    public static final DomainPermission TRIPS__MANAGE_TRIP = new DomainPermission("/app/trips/manage-trip");
    public static final DomainPermission TRIPS__VIEW_TRIP = new DomainPermission("/app/trips/view-trip");

    public static final DomainPermission STUDENTS__EDIT_STUDENT = new DomainPermission("/app/students/edit");

}
