package com.wellnr.schooltrip.core.model.user;

import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "apply")
public class AuthenticatedUser implements User {

    String username;

    @Override
    public boolean hasSinglePermission(DomainPermission permission) {
        return true;
    }

}
