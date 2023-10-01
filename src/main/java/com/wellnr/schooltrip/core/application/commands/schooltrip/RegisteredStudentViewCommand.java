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

import java.util.Map;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RegisteredStudentViewCommand implements AbstractSchoolTripCommand<DataResult<RegisteredStudentViewCommand.StudentProjection>> {

    String token;

    @Override
    public DataResult<StudentProjection> run(User user, SchoolTripDomainRegistry domainRegistry) {

        var student = domainRegistry.getStudents().getStudentByConfirmationToken(token);

        var schoolTrip = domainRegistry
            .getSchoolTrips()
            .getSchoolTripById(student.getSchoolTrip().schoolTripId());

        var paymentLinks = student.getPaymentLinks(
            domainRegistry.getSchoolTrips(), user.getMessages()
        );

        return DataResult.apply(new StudentProjection(student, schoolTrip, paymentLinks));
    }

    public record StudentProjection(Student student, SchoolTrip schoolTrip, Map<String, String> paymentLinks) {

    }

}
