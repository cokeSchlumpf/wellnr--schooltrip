package com.wellnr.schooltrip.core.application.commands.schooltrip;

import com.wellnr.ddd.commands.DataResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripDetailsProjection;
import com.wellnr.schooltrip.core.model.user.User;
import com.wellnr.schooltrip.core.model.user.exceptions.NotAuthorizedException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetSchoolTripDetailsCommand implements AbstractSchoolTripCommand<DataResult<SchoolTripDetailsProjection>> {

    String name;

    @Override
    public DataResult<SchoolTripDetailsProjection> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var trip = domainRegistry.getSchoolTrips().getSchoolTripByName(name);

        if (!trip.canBeAccessedByUser(user)) {
            throw NotAuthorizedException.apply();
        }

        var students = trip.getRegisteredStudents(user, domainRegistry.getStudents());
        var managers = trip.getManagers(domainRegistry.getUsers());

        return DataResult.apply(new SchoolTripDetailsProjection(trip, students, managers));
    }

}
