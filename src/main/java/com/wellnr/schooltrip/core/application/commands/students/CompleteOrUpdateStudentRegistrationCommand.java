package com.wellnr.schooltrip.core.application.commands.students;

import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.student.questionaire.Questionnaire;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CompleteOrUpdateStudentRegistrationCommand implements AbstractSchoolTripCommand<MessageResult<Nothing>> {

    String token;

    Questionnaire questionnaire;

    String notificationEmail;

    @Override
    public MessageResult<Nothing> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var student = domainRegistry.getStudents().getStudentByToken(token);

        student.completeStudentRegistration(
            questionnaire, notificationEmail, domainRegistry.getStudents(),domainRegistry.getSchoolTrips(),
            domainRegistry.getMailSender(), domainRegistry.getConfig(), user.getMessages()
        );

        return MessageResult.formatted(
            "Successfully updated `%s`.", student.getDisplayName()
        );
    }

}
