package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.CommandResult;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.student.Gender;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RegisterStudentCommand implements DomainCommand {

    String schoolTrip;

    String schoolClass;

    String firstName;

    String lastName;

    @Override
    public MessageResult<Nothing> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var schoolTrip = domainRegistry
            .getSchoolTrips()
            .getSchoolTripByName(this.schoolTrip);

        var schoolTripId =new SchoolTripId(schoolTrip.getId());

        var student = Student.createNew(
            schoolTripId, schoolClass, firstName, lastName,
            Instant.now(), Gender.Male
        );

        student.register(
            user, domainRegistry.getSchoolTrips(), domainRegistry.getStudents(),
            domainRegistry.getValidation()
        );

        return MessageResult.formatted("Successfully created student `%s %s`", firstName, lastName);
    }

}
