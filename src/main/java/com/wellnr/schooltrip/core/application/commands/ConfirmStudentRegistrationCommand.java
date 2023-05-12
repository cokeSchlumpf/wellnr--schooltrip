package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ConfirmStudentRegistrationCommand implements AbstractSchoolTripCommand<MessageResult<Nothing>> {

    String token;

    @Override
    public MessageResult<Nothing> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var student = domainRegistry.getStudents().getStudentByConfirmationToken(token);
        student.confirmStudentRegistration(domainRegistry.getStudents());

        return MessageResult.formatted(
            "Successfully confirmed registration.", student.getDisplayName()
        );
    }

}
