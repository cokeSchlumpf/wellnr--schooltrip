package com.wellnr.schooltrip.core.model.user;

import com.wellnr.schooltrip.core.model.user.rbac.DomainPermission;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.Locale;
import java.util.Optional;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(staticName = "apply")
public class AnonymousUser implements User {

    String preferredLocale;

    public static AnonymousUser apply() {
        return apply(null);
    }

    @Override
    public Optional<RegisteredUser> getRegisteredUser() {
        return Optional.empty();
    }

    @Override
    public boolean hasSinglePermission(DomainPermission permission) {
        return false;
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
