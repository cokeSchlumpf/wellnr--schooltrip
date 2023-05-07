package com.wellnr.schooltrip.core.model.user;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class AnonymousUser implements User {

    @Override
    public boolean hasSinglePermission(DomainPermission permission) {
        return false;
    }

}
