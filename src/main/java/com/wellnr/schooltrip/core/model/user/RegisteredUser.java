package com.wellnr.schooltrip.core.model.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wellnr.common.markup.Done;
import com.wellnr.common.markup.Result;
import com.wellnr.ddd.AggregateRoot;
import com.wellnr.schooltrip.core.ports.PasswordEncryptionPort;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.*;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisteredUser extends AggregateRoot<String, RegisteredUser> implements User {

    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String LAST_LOGIN = "lastLogin";
    private static final String DOMAIN_ROLES = "domainRoles";

    /**
     * The unique idempotent id of the user.
     */
    @JsonProperty(ID)
    String id;

    /**
     * The unique email-address of the user.
     */
    @JsonProperty(EMAIL)
    String email;

    /**
     * The (encrypted) password of the user.
     */
    @JsonProperty(PASSWORD)
    String password;

    /**
     * The first name of the user.
     */
    @JsonProperty(FIRST_NAME)
    String firstName;

    /**
     * The last name of the user.
     */
    @JsonProperty(LAST_NAME)
    String lastName;

    /**
     * Nullable. The time when the user last logged in, into the application.
     */
    @JsonProperty(LAST_LOGIN)
    Instant lastLogin;

    /**
     * The roles assigned to the user.
     */
    @JsonProperty(DOMAIN_ROLES)
    Set<AssignedDomainRole> domainRoles;

    @JsonCreator
    public static RegisteredUser apply(
        @JsonProperty(ID) String id,
        @JsonProperty(EMAIL) String email,
        @JsonProperty(PASSWORD) String password,
        @JsonProperty(FIRST_NAME) String firstName,
        @JsonProperty(LAST_NAME) String lastName,
        @JsonProperty(LAST_LOGIN) Instant lastLogin,
        @JsonProperty(DOMAIN_ROLES) Set<AssignedDomainRole> domainRoles
    ) {
        if (Objects.isNull(domainRoles)) {
            domainRoles = Set.of();
        }

        return new RegisteredUser(
            id, email, password, firstName, lastName, lastLogin, Set.copyOf(domainRoles)
        );
    }

    public static RegisteredUser createNew(
        String email,
        String password,
        String firstName,
        String lastName,
        Set<AssignedDomainRole> domainRoles,
        PasswordEncryptionPort passwordEncryptionPort
    ) {
        if (Objects.isNull(domainRoles)) {
            domainRoles = Set.of();
        }

        var id = UUID.randomUUID().toString();
        var passwordEncrypted = passwordEncryptionPort.encode(password);

        return new RegisteredUser(
            id, email, passwordEncrypted, firstName, lastName, Instant.now(), Set.copyOf(domainRoles)
        );
    }

    public static RegisteredUser fake() {
        return RegisteredUser.apply("123", "info@bar.de", "blaböa", "Egon", "Olsen", Instant.now(), Set.of());
    }

    /**
     * Initially creates (persists) this registered user instance.
     *
     * @param users The repository to persist the information.
     */
    public void register(
        RegisteredUsersRepository users
    ) {
        var existing = users.findOneByEmail(this.email);

        if (existing.isPresent() && !existing.get().id.equals(this.id)) {
            throw UserAlreadyExistsException.apply();
        } else {
            this.registerEvent(RegisteredUserRegisteredEvent.apply(this));
            users.insertOrUpdate(this);
        }
    }

    public Optional<Instant> getLastLogin() {
        return Optional.ofNullable(lastLogin);
    }

    @Override
    public boolean hasSinglePermission(DomainPermission permission) {
        var allPermissions = getPermissions();

        return allPermissions
            .stream()
            .anyMatch(p -> p.equals(permission));
    }

    public Result<Done> login(
        String password, RegisteredUsersRepository users, PasswordEncryptionPort encryptionPort
    ) {

        if (encryptionPort.matches(password, this.getPassword())) {
            // Valid login
            this.setLastLogin(Instant.now());
            users.insertOrUpdate(this);

            return Result.success(Done.getInstance());
        } else {
            // Invalid login
            // TODO: Add number of failed logins and lock account on too many retries.
            return Result.failure(NotAuthorizedException.apply());
        }
    }

    private List<DomainPermission> getPermissions() {
        return this
            .domainRoles
            .stream()
            .flatMap(role -> role.getPermissions().stream())
            .toList();
    }

}
