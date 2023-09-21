package com.wellnr.schooltrip.core.model.user;

import com.wellnr.schooltrip.core.model.user.rbac.DomainPermission;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public class AuthenticatedUser implements User {

    String username;

    @Override
    public Optional<RegisteredUser> getRegisteredUser() {
        return Optional.empty();
    }

    @Override
    public boolean hasSinglePermission(DomainPermission permission) {
        return true;
    }

}
