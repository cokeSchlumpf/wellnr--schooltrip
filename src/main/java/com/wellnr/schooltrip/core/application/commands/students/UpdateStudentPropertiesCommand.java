package com.wellnr.schooltrip.core.application.commands.students;

import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.student.Gender;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class UpdateStudentPropertiesCommand implements AbstractSchoolTripCommand<MessageResult<Student>> {

    final String id;
    String schoolClass;
    String firstName;
    String lastName;
    LocalDate birthday;
    Gender gender;

    public static UpdateStudentPropertiesCommand apply(Student student) {
        return new UpdateStudentPropertiesCommand(
            student.getId(),
            student.getSchoolClass(),
            student.getFirstName(),
            student.getLastName(),
            student.getBirthday(),
            student.getGender()
        );
    }

    @Override
    public MessageResult<Student> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var student = domainRegistry.getStudents().getStudentById(id);

        student.updateStudentProperties(
            schoolClass, firstName, lastName, birthday, gender,
            domainRegistry.getStudents(), domainRegistry.getSchoolTrips()
        );

        return MessageResult
            .apply(user.getMessages().successfullyUpdatedStudent(student.getDisplayName()))
            .withData(student);
    }

}
