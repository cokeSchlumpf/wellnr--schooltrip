package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.ddd.commands.DataResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetSchoolTripDetailsCommand implements AbstractSchoolTripCommand<DataResult<GetSchoolTripDetailsCommand.SchoolTripDetailsProjection>> {

    String name;

    @Override
    public DataResult<SchoolTripDetailsProjection> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var trip = domainRegistry.getSchoolTrips().getSchoolTripByName(name);
        var students = trip.getRegisteredStudents(user, domainRegistry.getStudents());

        return DataResult.apply(new SchoolTripDetailsProjection(trip, students));
    }

    public record SchoolTripDetailsProjection(
        SchoolTrip schoolTrip,
        List<Student> students
    ) {

    }

}
