package com.wellnr.schooltrip.core.model.user;

import com.wellnr.schooltrip.core.model.user.exceptions.NotAuthorizedException;
import com.wellnr.schooltrip.core.model.user.rbac.DomainPermission;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface User {

    default void checkPermission(DomainPermission... permission) {
        if (!this.hasPermission(permission)) {
            throw NotAuthorizedException.apply();
        }
    }

    default void checkPermission(List<DomainPermission> permissions) {
        if (!this.hasPermission(permissions)) {
            throw NotAuthorizedException.apply();
        }
    }

    Optional<RegisteredUser> getRegisteredUser();

    default boolean hasPermission(DomainPermission... permission) {
        return Arrays
            .stream(permission)
            .anyMatch(this::hasSinglePermission);
    }

    default boolean hasPermission(List<DomainPermission> permissions) {
        return permissions
            .stream()
            .anyMatch(this::hasSinglePermission);
    }

    boolean hasSinglePermission(DomainPermission permission);

}
