package com.wellnr.schooltrip.infrastructure;

import com.wellnr.ddd.commands.CommandResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SchoolTripCommandRunner {

    private final SchoolTripDomainRegistry domainRegistry;

    public <T extends CommandResult> T run(AbstractSchoolTripCommand<T> command) {
        return command.run(
            domainRegistry.getUsers().getOneByEmail("michael.wellner@gmail.com"),
            domainRegistry
        );
    }

}
