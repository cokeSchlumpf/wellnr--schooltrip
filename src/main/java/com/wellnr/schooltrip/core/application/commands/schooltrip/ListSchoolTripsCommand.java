package com.wellnr.schooltrip.core.application.commands.schooltrip;

import com.wellnr.ddd.commands.DataResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(staticName = "apply")
public class ListSchoolTripsCommand implements AbstractSchoolTripCommand<DataResult<List<SchoolTrip>>> {

    @Override
    public DataResult<List<SchoolTrip>> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var trips = domainRegistry
            .getSchoolTrips()
            .findAllSchoolTrips()
            .stream()
            .filter(trip -> trip.canBeAccessedByUser(user))
            .toList();

        return DataResult.apply(trips);
    }

}
