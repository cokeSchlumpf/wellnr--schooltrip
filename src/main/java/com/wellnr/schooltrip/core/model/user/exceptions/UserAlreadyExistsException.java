package com.wellnr.schooltrip.core.model.user.exceptions;

import com.wellnr.ddd.DomainException;

public class UserAlreadyExistsException extends DomainException {

    private UserAlreadyExistsException() {
        super("User already exists.");
    }

    public static UserAlreadyExistsException apply() {
        return new UserAlreadyExistsException();
    }

}
