package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.CommandResult;
import com.wellnr.ddd.commands.DataResult;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.user.AssignedDomainRole;
import com.wellnr.schooltrip.core.model.user.DomainRoles;
import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.net.URI;
import java.util.Set;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RegisterAdminUserCommand implements AbstractSchoolTripCommand<MessageResult<RegisteredUser>> {

    String email;

    String password;

    String firstName;

    String lastName;

    @Override
    public MessageResult<RegisteredUser> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var newUser = RegisteredUser.createNew(
            email, password, firstName, lastName, Set.of(
                AssignedDomainRole.apply(
                    DomainRoles.APP_ADMINISTRATOR.getName(),
                    URI.create("urn:app")
                )
            ),
            domainRegistry.getPasswordEncryptionPort()
        );

        newUser.register(domainRegistry.getUsers());
        return MessageResult.formatted("Successfully created user `%s`.", email).withData(newUser);
    }

}
