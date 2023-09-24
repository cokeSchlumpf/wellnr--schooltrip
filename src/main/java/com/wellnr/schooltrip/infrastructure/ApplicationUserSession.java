package com.wellnr.schooltrip.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellnr.common.Operators;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.users.GetUserApplicationPermissionsCommand;
import com.wellnr.schooltrip.core.model.user.*;
import com.wellnr.schooltrip.core.model.user.exceptions.NotAuthorizedException;
import com.wellnr.schooltrip.core.model.user.rbac.DomainPermission;
import com.wellnr.schooltrip.core.model.user.rbac.DomainRole;
import com.wellnr.schooltrip.core.ports.PasswordEncryptionPort;
import com.wellnr.schooltrip.core.ports.i18n.I18N;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@SessionScope
public class ApplicationUserSession {

    private final SchoolTripDomainRegistry domainRegistry;

    private final RegisteredUsersRepository users;

    private final PasswordEncryptionPort passwordEncryption;

    private final JwtEncoder jwtEncoder;

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private User user;

    private GetUserApplicationPermissionsCommand.ApplicationPermissions permissions;

    private SchoolTripMessages i18n;

    public ApplicationUserSession(SchoolTripDomainRegistry domainRegistry, JwtEncoder jwtEncoder, ObjectMapper om,
                                  HttpServletRequest ctx) {
        this.domainRegistry = domainRegistry;
        this.users = domainRegistry.getUsers();
        this.passwordEncryption = domainRegistry.getPasswordEncryptionPort();
        this.jwtEncoder = jwtEncoder;
        this.objectMapper = om;
        this.request = ctx;
    }

    public SchoolTripMessages getMessages() {
        if (this.i18n == null) {
            var locale = getPreferredLocale();
            i18n = I18N.createInstance(SchoolTripMessages.class, locale);
        }

        return i18n;
    }

    public boolean isGerman() {
        var locale = getPreferredLocale();
        return locale.equals(Locale.GERMAN) || locale.equals(Locale.GERMANY);
    }

    public boolean isEnglish() {
        return !isGerman();
    }

    public GetUserApplicationPermissionsCommand.ApplicationPermissions getPermissions() {
        if (Objects.isNull(permissions)) {
            this.permissions = GetUserApplicationPermissionsCommand
                .apply()
                .run(this.getUser(), domainRegistry)
                .getData();
        }

        return permissions;
    }

    public Locale getPreferredLocale() {
        if (Objects.isNull(user)) {
            return request.getLocale();
        } else {
            return user.getPreferredLocale().orElse(request.getLocale());
        }
    }

    public Optional<RegisteredUser> getRegisteredUser() {
        return getUser().getRegisteredUser();
    }

    public void setRegisteredUser(RegisteredUser user) {
        this.logout();
        this.user = user;
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

                    user = new JWTUser(users, email, permissions, request.getLocale().toLanguageTag());
                } else {
                    user = AuthenticatedUser.apply(jwt.getTokenValue());
                }
            } else {
                user = AnonymousUser.apply(request.getLocale().toLanguageTag());
            }
        }

        return user;
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
                    .expiresAt(now.plus(6, ChronoUnit.HOURS))
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
                Operators.suppressExceptions(() -> {
                    throw loginResult.getException();
                });
                return "";
            }
        } else {
            throw NotAuthorizedException.apply();
        }
    }

    public void logout() {
        this.user = AnonymousUser.apply(request.getLocale().toLanguageTag());
        this.i18n = null;
    }

    public void setLocale(Locale locale) {
        this.user.setPreferredLocale(locale, users);
        this.i18n = I18N.createInstance(SchoolTripMessages.class, locale);
    }

    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    private static class JWTUser implements User {

        private final RegisteredUsersReadRepository users;

        private final String username;

        private final List<DomainPermission> permissions;

        private String locale;

        @Override
        public Optional<RegisteredUser> getRegisteredUser() {
            return users.findOneByEmail(username);
        }

        @Override
        public boolean hasSinglePermission(DomainPermission permission) {
            return permissions.stream().anyMatch(p -> p.equals(permission));
        }

        @Override
        public Optional<Locale> getPreferredLocale() {
            return Optional
                .ofNullable(this.locale)
                .map(Locale::forLanguageTag);
        }

        @Override
        public void setPreferredLocale(Locale locale, RegisteredUsersRepository users) {
            this.locale = locale.toLanguageTag();
        }

    }

}
