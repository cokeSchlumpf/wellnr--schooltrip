package com.wellnr.schooltrip.core.model.user;

import java.util.Arrays;

public interface User {

    boolean hasSinglePermission(DomainPermission permission);

    default boolean hasPermission(DomainPermission ...permission) {
        return Arrays
            .stream(permission)
            .anyMatch(this::hasSinglePermission);
    }

    default void checkPermission(DomainPermission ...permission) {
        if (!this.hasPermission(permission)) {
            throw NotAuthorizedException.apply();
        }
    }

}
