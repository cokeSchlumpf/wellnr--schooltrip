package com.wellnr.schooltrip.infrastructure;

import com.wellnr.schooltrip.SchooltripApplication;
import com.wellnr.schooltrip.core.model.user.*;
import com.wellnr.schooltrip.core.model.user.rbac.DomainPermission;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Objects;

/**
 * This interceptor checks for an existing JWT authenticated user. If yes,
 * an instance of {@link User} is injected into the request context.
 */
@Component
@AllArgsConstructor
public class SchoolTripAuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getPrincipal() instanceof Jwt jwt) {
            var rolesRaw = jwt.getClaims().get("roles");

            if (Objects.nonNull(rolesRaw) && rolesRaw instanceof List<?> rolesList) {
                var permissions = rolesList
                    .stream()
                    .map(Object::toString)
                    .map(AssignedDomainRole::fromStringProjection)
                    .flatMap(role -> role.getPermissions().stream())
                    .toList();

                var user = new JWTUser(permissions);
                request.setAttribute(
                    SchooltripApplication.USER_CONTEXT_ATTRIBUTE, user
                );
            } else {
                var user = AuthenticatedUser.apply(jwt.getTokenValue());

                request.setAttribute(
                    SchooltripApplication.USER_CONTEXT_ATTRIBUTE, user
                );
            }
        } else {
            request.setAttribute(
                SchooltripApplication.USER_CONTEXT_ATTRIBUTE, AnonymousUser.apply()
            );
        }

        return true;
    }

    private record JWTUser(List<DomainPermission> permissions) implements User {

        @Override
        public boolean hasSinglePermission(DomainPermission permission) {
            return permissions.stream().anyMatch(p -> p.equals(permission));
        }

    }

}
