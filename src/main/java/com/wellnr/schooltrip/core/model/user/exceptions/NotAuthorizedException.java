package com.wellnr.schooltrip.core.model.user.exceptions;

import com.wellnr.ddd.DomainException;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;

public class NotAuthorizedException extends DomainException {

    private NotAuthorizedException() {
        super("Not authorized.");
    }

    public static NotAuthorizedException apply() {
        return new NotAuthorizedException();
    }

    @Override
    public String getUserMessage(SchoolTripMessages i18n) {
        return i18n.notAuthorized();
    }
}
