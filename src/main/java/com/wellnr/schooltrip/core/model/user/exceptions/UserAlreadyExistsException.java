package com.wellnr.schooltrip.core.model.user.exceptions;

import com.wellnr.ddd.DomainException;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;

public class UserAlreadyExistsException extends DomainException {

    private UserAlreadyExistsException() {
        super("User already exists.");
    }

    public static UserAlreadyExistsException apply() {
        return new UserAlreadyExistsException();
    }

    @Override
    public String getUserMessage(SchoolTripMessages i18n) {
        return i18n.userAlreadyExists();
    }

}
