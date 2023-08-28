package com.wellnr.schooltrip.core.application.commands.students;

import com.wellnr.ddd.DomainException;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.application.commands.schooltrip.RegisterSchoolClassCommand;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RegisterStudentsCommand implements AbstractSchoolTripCommand<MessageResult<List<DomainException>>> {

    String schoolTrip;

    List<RegisterStudentCommand> commandList;

    @Override
    public MessageResult<List<DomainException>> run(User user, SchoolTripDomainRegistry domainRegistry) {
        /*
         * Create classes.
         */
        List<DomainException> errors = new ArrayList<>();

        commandList
            .stream()
            .map(RegisterStudentCommand::getSchoolClass)
            .collect(Collectors.toSet())
            .stream()
            .map(schoolClass -> RegisterSchoolClassCommand.apply(
                schoolTrip,
                schoolClass
            ))
            .forEach(cmd -> {
                try {
                    cmd.run(user, domainRegistry);
                } catch (DomainException ex) {
                    errors.add(ex);
                }
            });

        /*
         * Register students.
         */
        commandList.forEach(cmd -> {
            try {
                cmd.run(user, domainRegistry);
            } catch (DomainException ex) {
                errors.add(ex);
            }
        });

        if (errors.isEmpty()) {
            return MessageResult
                .formatted("Successfully registered students from Excel.")
                .withData(errors);
        } else {
            return MessageResult
                .formatted("Successfully registered students, but `%d` errors occurred.", errors.size())
                .withData(errors);
        }
    }

}
