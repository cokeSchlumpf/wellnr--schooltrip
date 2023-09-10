package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class UpdateRegisteredUserCommand implements AbstractSchoolTripCommand<MessageResult<RegisteredUser>> {

    String oldEmail;

    String newEmail;

    String firstName;

    String lastName;

    String preferredLocale;

    @Override
    public MessageResult<RegisteredUser> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var registeredUser = domainRegistry.getUsers().getOneByEmail(oldEmail);
        registeredUser.updateProperties(
            user, firstName, lastName, newEmail,
            preferredLocale != null && preferredLocale.trim().length() > 0 ? Locale.forLanguageTag(preferredLocale) : null,
            domainRegistry.getUsers()
        );

        return MessageResult.formatted("Successfully updated user `%s`.", newEmail).withData(registeredUser);
    }

}
