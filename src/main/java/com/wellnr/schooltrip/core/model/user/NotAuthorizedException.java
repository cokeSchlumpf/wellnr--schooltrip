package com.wellnr.schooltrip.core.model.user;

import com.wellnr.ddd.DomainException;

public class NotAuthorizedException extends DomainException {

    private NotAuthorizedException() {
        super("Not authorized foo bar.");
    }

    public static NotAuthorizedException apply() {
        return new NotAuthorizedException();
    }

}
