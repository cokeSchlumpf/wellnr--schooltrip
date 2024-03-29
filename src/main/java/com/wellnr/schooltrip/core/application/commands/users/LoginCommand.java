package com.wellnr.schooltrip.core.application.commands.users;

import com.wellnr.common.markup.Result;
import com.wellnr.common.markup.When;
import com.wellnr.ddd.DomainException;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import com.wellnr.schooltrip.core.model.user.User;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class LoginCommand implements AbstractSchoolTripCommand<MessageResult<Result<RegisteredUser>>> {

    @Email
    @NotBlank
    String username;

    @NotBlank
    String password;

    @Override
    public MessageResult<Result<RegisteredUser>> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var loginResult = domainRegistry
            .getUsers()
            .findOneByEmail(username)
            .map(registeredUser -> registeredUser
                .login(
                    password, domainRegistry.getUsers(), domainRegistry.getPasswordEncryptionPort()
                )
                .mapSuccess(
                    done -> registeredUser
                )
            )
            .orElse(Result.failure(new NotSuccessfulException()));

        var message = When
            .isTrue(loginResult.isFailure())
            .then(user.getMessages().loginFailed())
            .otherwise(user.getMessages().loginSucceeded());

        return MessageResult.apply(message, loginResult);
    }

    public static class NotSuccessfulException extends DomainException {

        public NotSuccessfulException() {
            super("Login was not successful.");
        }

        @Override
        public String getUserMessage(SchoolTripMessages i18n) {
            return i18n.loginFailed();
        }

    }

}
