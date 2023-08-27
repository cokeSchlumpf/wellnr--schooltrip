package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.*;

import java.util.Objects;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ResetPasswordCommand implements AbstractSchoolTripCommand<MessageResult<RegisteredUser>> {

    String email;

    String newPassword;

    String newPasswordRepeated;

    @Override
    public MessageResult<RegisteredUser> run(User user, SchoolTripDomainRegistry domainRegistry) {
        Objects.requireNonNull(email);
        Objects.requireNonNull(newPassword);
        Objects.requireNonNull(newPasswordRepeated);

        var registeredUser = domainRegistry.getUsers().getOneByEmail(email);
        registeredUser.resetPassword(
            user, newPassword, newPasswordRepeated,
            domainRegistry.getUsers(), domainRegistry.getPasswordEncryptionPort()
        );

        return MessageResult.formatted("Successfully updated password for user `%s`.", email).withData(registeredUser);
    }

}
