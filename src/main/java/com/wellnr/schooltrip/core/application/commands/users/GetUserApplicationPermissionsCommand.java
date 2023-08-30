package com.wellnr.schooltrip.core.application.commands.users;

import com.wellnr.ddd.commands.DataResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.user.User;
import com.wellnr.schooltrip.core.model.user.rbac.DomainPermissions;
import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor(staticName = "apply")
public class GetUserApplicationPermissionsCommand implements AbstractSchoolTripCommand<DataResult<GetUserApplicationPermissionsCommand.ApplicationPermissions>> {

    @Override
    public DataResult<ApplicationPermissions> run(User user, SchoolTripDomainRegistry domainRegistry) {
        return DataResult.apply(ApplicationPermissions.apply(
            user.hasPermission(DomainPermissions.ManageApplication.apply()),
            user.hasPermission(DomainPermissions.ManageSchoolTrips.apply())
        ));
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class ApplicationPermissions {

        boolean canManageApplication;

        boolean canManageSchoolTrips;

    }

}
