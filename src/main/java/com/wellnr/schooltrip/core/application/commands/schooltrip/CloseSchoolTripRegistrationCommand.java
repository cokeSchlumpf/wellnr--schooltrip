package com.wellnr.schooltrip.core.application.commands.schooltrip;

import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.student.questionaire.Questionaire;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CloseSchoolTripRegistrationCommand implements AbstractSchoolTripCommand<MessageResult<Nothing>> {

    SchoolTripId id;

    @Override
    public MessageResult<Nothing> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var removedStudents = domainRegistry
            .getSchoolTrips()
            .getSchoolTripById(this.id.schoolTripId())
            .closeRegistration(
                user, domainRegistry.getSchoolTrips(), domainRegistry.getStudents()
            );

        return MessageResult.formatted(
            "Closed registration and removed %d students who have not been registered yet.", removedStudents.size()
        );
    }

}
