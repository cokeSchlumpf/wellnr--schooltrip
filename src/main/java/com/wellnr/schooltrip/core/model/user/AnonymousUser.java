package com.wellnr.schooltrip.core.model.user;

import com.wellnr.schooltrip.core.model.user.rbac.DomainPermission;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Optional;

@Value
@AllArgsConstructor(staticName = "apply")
public class AnonymousUser implements User {

    @Override
    public boolean hasSinglePermission(DomainPermission permission) {
        return false;
    }

    @Override
    public Optional<RegisteredUser> getRegisteredUser() {
        return Optional.empty();
    }

}
