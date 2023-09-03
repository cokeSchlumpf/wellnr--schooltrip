package com.wellnr.schooltrip.core.application.commands.schooltrip;

import com.wellnr.ddd.commands.DataResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ExportInviteMailingLetterData implements AbstractSchoolTripCommand<DataResult<Path>> {

    String schoolTrip;

    @Override
    public DataResult<Path> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var path = domainRegistry
            .getSchoolTrips()
            .getSchoolTripByName(schoolTrip)
            .exportInviteLetterMailingData(user, domainRegistry.getStudents(), domainRegistry.getConfig());

        return DataResult.apply(path);
    }

}
