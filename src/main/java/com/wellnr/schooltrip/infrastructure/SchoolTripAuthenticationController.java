package com.wellnr.schooltrip.infrastructure;

import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.user.AssignedDomainRole;
import com.wellnr.schooltrip.core.model.user.exceptions.NotAuthorizedException;
import com.wellnr.schooltrip.core.ports.PasswordEncryptionPort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class SchoolTripAuthenticationController {

    private final JwtEncoder jwtEncoder;

    private final HttpServletRequest request;

    private final SchoolTripDomainRegistry domainRegistry;

    private final PasswordEncryptionPort passwordEncryption;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) throws Exception {
        var maybeRegisteredUser = domainRegistry.getUsers().findOneByEmail(request.username);

        if (maybeRegisteredUser.isPresent()) {
            var user = maybeRegisteredUser.get();

            var loginResult = user.login(
                request.getPassword(), domainRegistry.getUsers(), passwordEncryption
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
                    .claim("roles", user.getDomainRoles().stream().map(AssignedDomainRole::toStringProjection).collect(Collectors.toSet()))
                    .build();

                return jwtEncoder
                    .encode(JwtEncoderParameters.from(claims))
                    .getTokenValue();
            } else {
                // TODO: Don't throw exception - This would roll back transactions?
                throw loginResult.getException();
            }
        } else {
            throw NotAuthorizedException.apply();
        }
    }

    @PostMapping("/test")
    public String test() {
        return (request.getAttribute("APP__USER").toString());
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    public static class LoginRequest {

        String username;

        String password;

    }

}
