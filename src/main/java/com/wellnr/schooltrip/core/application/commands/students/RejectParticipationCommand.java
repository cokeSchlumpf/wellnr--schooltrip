package com.wellnr.schooltrip.core.application.commands.students;

import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.student.RejectionReason;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RejectParticipationCommand implements AbstractSchoolTripCommand<MessageResult<Nothing>> {

    String token;

    RejectionReason rejectionReason;

    @Override
    public MessageResult<Nothing> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var student = domainRegistry.getStudents().getStudentByToken(token);

        student.rejectParticipation(
            user,
            rejectionReason,
            domainRegistry.getStudents(),
            domainRegistry.getSchoolTrips(),
            domainRegistry.getMailSender(),
            user.getMessages(),
            domainRegistry.getConfig()
        );

        return MessageResult.apply("Registration has been rejected.");
    }

}
