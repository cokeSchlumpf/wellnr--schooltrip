package com.wellnr.schooltrip.core.model.user;

import com.wellnr.schooltrip.core.model.user.rbac.DomainPermission;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "apply")
public class AuthenticatedUser implements User {

    String username;

    @Override
    public boolean hasSinglePermission(DomainPermission permission) {
        return true;
    }

}
