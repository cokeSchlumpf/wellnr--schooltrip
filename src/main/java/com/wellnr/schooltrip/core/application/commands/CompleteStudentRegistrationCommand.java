package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.student.questionaire.Questionaire;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CompleteStudentRegistrationCommand implements AbstractSchoolTripCommand<MessageResult<Nothing>> {

    String token;

    Questionaire questionaire;

    String notificationEmail;

    @Override
    public MessageResult<Nothing> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var student = domainRegistry.getStudents().getStudentByToken(token);
        student.completeStudentRegistration(
            questionaire, notificationEmail, domainRegistry.getStudents(),
            domainRegistry.getMailSender(), domainRegistry.getMessages()
        );

        return MessageResult.formatted(
            "Successfully updated `%s`.", student.getDisplayName()
        );
    }

}
