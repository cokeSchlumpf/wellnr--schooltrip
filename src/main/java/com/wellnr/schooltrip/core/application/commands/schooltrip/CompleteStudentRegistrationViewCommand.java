package com.wellnr.schooltrip.core.application.commands.schooltrip;

import com.wellnr.ddd.commands.DataResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CompleteStudentRegistrationViewCommand implements AbstractSchoolTripCommand<DataResult<CompleteStudentRegistrationViewCommand.StudentProjection>> {

    String token;

    @Override
    public DataResult<StudentProjection> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var student = domainRegistry.getStudents().getStudentByToken(token);
        var schoolTrip = domainRegistry
            .getSchoolTrips()
            .getSchoolTripById(student.getSchoolTrip().schoolTripId());

        return DataResult.apply(new StudentProjection(student, schoolTrip));
    }

    public record StudentProjection(Student student, SchoolTrip schoolTrip) {

    }

}
