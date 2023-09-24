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
public class CompleteOrUpdateStudentRegistrationByOrganizerCommand implements AbstractSchoolTripCommand<MessageResult<Nothing>> {

    String studentId;

    Questionnaire questionnaire;

    @Override
    public MessageResult<Nothing> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var student = domainRegistry.getStudents().getStudentById(studentId);
        student.completeOrUpdateStudentRegistrationByOrganizer(
            questionnaire, domainRegistry.getStudents()
        );

        System.out.println(questionnaire);

        return MessageResult.formatted(
            "Successfully registered student `%s`.", student.getDisplayName()
        );
    }

}
