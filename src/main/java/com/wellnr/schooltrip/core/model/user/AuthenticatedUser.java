package com.wellnr.schooltrip.core.model.user;

import com.wellnr.schooltrip.core.model.user.rbac.DomainPermission;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Locale;
import java.util.Optional;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(staticName = "apply")
public class AuthenticatedUser implements User {

    String username;

    String preferredLocale;

    public static AuthenticatedUser apply(String username) {
        return apply(username, null);
    }

    @Override
    public Optional<RegisteredUser> getRegisteredUser() {
        return Optional.empty();
    }

    @Override
    public boolean hasSinglePermission(DomainPermission permission) {
        return true;
    }

    @Override
    public Optional<Locale> getPreferredLocale() {
        return Optional
            .ofNullable(this.preferredLocale)
            .map(Locale::forLanguageTag);
    }

    @Override
    public void setPreferredLocale(Locale locale, RegisteredUsersRepository users) {
        this.preferredLocale = locale.toLanguageTag();
    }

}
