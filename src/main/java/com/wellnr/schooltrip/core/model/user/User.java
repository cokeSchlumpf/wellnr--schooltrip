package com.wellnr.schooltrip.core.model.user;

import com.wellnr.schooltrip.core.model.user.exceptions.NotAuthorizedException;
import com.wellnr.schooltrip.core.model.user.rbac.DomainPermission;
import com.wellnr.schooltrip.core.ports.i18n.I18N;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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

    Optional<Locale> getPreferredLocale();

    default SchoolTripMessages getMessages() {
        var locale = getPreferredLocale().orElse(Locale.ENGLISH);
        return I18N.createInstance(SchoolTripMessages.class, locale);
    }

    void setPreferredLocale(Locale locale, RegisteredUsersRepository users);

}
