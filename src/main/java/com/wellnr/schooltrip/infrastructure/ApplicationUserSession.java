package com.wellnr.schooltrip.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellnr.common.Operators;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.user.*;
import com.wellnr.schooltrip.core.model.user.exceptions.NotAuthorizedException;
import com.wellnr.schooltrip.core.model.user.rbac.DomainPermission;
import com.wellnr.schooltrip.core.model.user.rbac.DomainRole;
import com.wellnr.schooltrip.core.ports.PasswordEncryptionPort;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequestScope
public class ApplicationUserSession {

    private final RegisteredUsersRepository users;

    private final PasswordEncryptionPort passwordEncryption;

    private final JwtEncoder jwtEncoder;

    private final ObjectMapper objectMapper;

    User user;

    public ApplicationUserSession(SchoolTripDomainRegistry domainRegistry, JwtEncoder jwtEncoder, ObjectMapper om) {
        this.users = domainRegistry.getUsers();
        this.passwordEncryption = domainRegistry.getPasswordEncryptionPort();
        this.jwtEncoder = jwtEncoder;
        this.objectMapper = om;
    }

    public User getUser() {
        if (Objects.isNull(user)) {
            var auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth.getPrincipal() instanceof Jwt jwt) {
                var rolesRaw = jwt.getClaims().get("roles");
                var email = jwt.getClaims().get("email").toString();

                if (Objects.nonNull(rolesRaw) && rolesRaw instanceof List<?> rolesList) {
                    var permissions = rolesList
                        .stream()
                        .map(Object::toString)
                        .map(s -> Operators.suppressExceptions(() -> objectMapper.readValue(s, DomainRole.class)))
                        .flatMap(role -> role.getPermissions().stream())
                        .toList();

                    user = new JWTUser(users, email, permissions);
                } else {
                    user = AuthenticatedUser.apply(jwt.getTokenValue());
                }
            } else {
                user = AnonymousUser.apply();
            }
        }

        return user;
    }

    public Optional<RegisteredUser> getRegisteredUser() {
        return getUser().getRegisteredUser();
    }

    public void setRegisteredUser(RegisteredUser user) {
        this.user = user;
    }

    public String login(String username, String password) {
        var maybeRegisteredUser = this.users.findOneByEmail(username);

        if (maybeRegisteredUser.isPresent()) {
            var user = maybeRegisteredUser.get();

            var loginResult = user.login(
                password, users, passwordEncryption
            );

            if (loginResult.isSuccess()) {
                var now = Instant.now();
                var claims = JwtClaimsSet
                    .builder()
                    .issuedAt(now)
                    .expiresAt(now.plus(1, ChronoUnit.HOURS))
                    .subject(user.getId())
                    .claim("firstName", user.getFirstName())
                    .claim("lastName", user.getLastName())
                    .claim("email", user.getEmail())
                    .claim(
                        "roles",
                        user
                            .getDomainRoles()
                            .stream()
                            .map(
                                role -> Operators.suppressExceptions(() -> objectMapper.writeValueAsString(role))
                            )
                            .collect(Collectors.toSet()))
                    .build();

                this.user = user;

                return jwtEncoder
                    .encode(JwtEncoderParameters.from(claims))
                    .getTokenValue();
            } else {
                // TODO: Don't throw exception - This would roll back transactions?
                Operators.suppressExceptions(() -> { throw loginResult.getException(); });
                return "";
            }
        } else {
            throw NotAuthorizedException.apply();
        }
    }

    public void logout() {
        this.user = AnonymousUser.apply();
    }

    @Value
    @AllArgsConstructor
    private static class JWTUser implements User {

        RegisteredUsersReadRepository users;

        String username;

        List<DomainPermission> permissions;

        @Override
        public boolean hasSinglePermission(DomainPermission permission) {
            return permissions.stream().anyMatch(p -> p.equals(permission));
        }

        @Override
        public Optional<RegisteredUser> getRegisteredUser() {
            return users.findOneByEmail(username);
        }

    }

}