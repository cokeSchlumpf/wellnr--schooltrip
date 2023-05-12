package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.ddd.commands.DataResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetStudentActionsForToken implements AbstractSchoolTripCommand<DataResult<Student>> {

    String token;

    @Override
    public DataResult<Student> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var student = domainRegistry.getStudents().getStudentByToken(token);
        return DataResult.apply(student);
    }

}
