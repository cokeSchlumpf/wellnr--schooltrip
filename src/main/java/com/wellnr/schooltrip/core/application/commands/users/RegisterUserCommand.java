package com.wellnr.schooltrip.core.application.commands.users;

import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.user.AssignedDomainRole;
import com.wellnr.schooltrip.core.model.user.rbac.DomainRoles;
import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.*;

import java.net.URI;
import java.util.HashSet;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RegisterUserCommand implements AbstractSchoolTripCommand<MessageResult<RegisteredUser>> {

    String email;

    String password;

    String firstName;

    String lastName;

    boolean admin;

    @Override
    public MessageResult<RegisteredUser> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var roles = new HashSet<AssignedDomainRole>();

        if (this.isAdmin()) {
            roles.add(AssignedDomainRole.apply(
                DomainRoles.APP_ADMINISTRATOR.getName(),
                URI.create("urn:app")
            ));
        }

        var newUser = RegisteredUser.createNew(
            email, password, firstName, lastName, roles,
            domainRegistry.getPasswordEncryptionPort()
        );

        newUser.register(domainRegistry.getUsers());
        return MessageResult.formatted("Successfully created user `%s`.", email).withData(newUser);
    }

}
