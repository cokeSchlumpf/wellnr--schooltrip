package com.wellnr.schooltrip.core.application.commands.users;

import com.wellnr.ddd.commands.DataResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(staticName = "apply")
public class ListUsersCommand implements AbstractSchoolTripCommand<DataResult<List<RegisteredUser>>> {

    @Override
    public DataResult<List<RegisteredUser>> run(User user, SchoolTripDomainRegistry domainRegistry) {
        return DataResult.apply(
            domainRegistry.getUsers().findAll()
        );
    }

}
