package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ReassignSchoolTripStudentIdsCommand implements AbstractSchoolTripCommand<MessageResult<Nothing>> {

    SchoolTripId id;

    @Override
    public MessageResult<Nothing> run(User user, SchoolTripDomainRegistry domainRegistry) {
        domainRegistry
            .getSchoolTrips()
            .getSchoolTripById(this.id.schoolTripId())
            .assignStudentIDs(
                user, domainRegistry.getSchoolTrips(), domainRegistry.getStudents()
            );

        return MessageResult.formatted(
            "Re-assigned school trip student ids."
        );
    }

}
