package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.ddd.commands.CommandResult;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RenameSchoolTripCommand implements DomainCommand {

    private String newTitle;

    @Override
    public CommandResult run(User user, SchoolTripDomainRegistry domainRegistry) {
        return MessageResult.formatted("Renamed trip to `%s`.", newTitle);
    }

}
