package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.ddd.commands.DataResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(staticName = "apply")
public class ListSchoolTripsCommand implements AbstractSchoolTripCommand<DataResult<List<SchoolTrip>>> {

    @Override
    public DataResult<List<SchoolTrip>> run(User user, SchoolTripDomainRegistry domainRegistry) {
        return DataResult.apply(
            domainRegistry.getSchoolTrips().findAllSchoolTrips().stream().toList()
        );
    }

}
