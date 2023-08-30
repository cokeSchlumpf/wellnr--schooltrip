package com.wellnr.schooltrip.core.application.commands.users;

import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import com.wellnr.schooltrip.core.model.user.User;
import com.wellnr.schooltrip.core.model.user.rbac.DomainRole;
import com.wellnr.schooltrip.core.model.user.rbac.DomainRoles;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        var roles = new HashSet<DomainRole>();

        if (this.isAdmin()) {
            roles.add(DomainRoles.ApplicationAdministrator.apply());
        }

        var newUser = RegisteredUser.createNew(
            email, password, firstName, lastName, roles,
            domainRegistry.getPasswordEncryptionPort()
        );

        newUser.register(domainRegistry.getUsers());
        return MessageResult.formatted("Successfully created user `%s`.", email).withData(newUser);
    }

}
