package com.wellnr.schooltrip.core.model.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wellnr.schooltrip.core.model.user.rbac.DomainPermission;
import com.wellnr.schooltrip.core.model.user.rbac.DomainRoles;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AssignedDomainRole {

    private static final String ROLE = "role";
    private static final String SUBJECT = "subject";

    @JsonProperty(ROLE)
    String role;

    @JsonProperty(SUBJECT)
    URI subject;

    @JsonCreator
    public static AssignedDomainRole apply(
        @JsonProperty(ROLE) String role,
        @JsonProperty(SUBJECT) URI subject) {

        return new AssignedDomainRole(role, subject);
    }

    public static AssignedDomainRole fromStringProjection(
        String projection
    ) {
        var parts = Arrays.stream(projection.split("@")).toList();
        return apply(parts.get(0), URI.create(parts.get(1)));
    }

    public List<DomainPermission> getPermissions() {
        return DomainRoles
            .getByName(this.role)
            .getPermissions()
            .stream()
            .map(permission -> permission.onSubject(this.subject))
            .toList();
    }

    public String toStringProjection() {
        return MessageFormat.format(
            "{0}@{1}",
            this.role,
            this.subject.toString()
        );
    }

}
