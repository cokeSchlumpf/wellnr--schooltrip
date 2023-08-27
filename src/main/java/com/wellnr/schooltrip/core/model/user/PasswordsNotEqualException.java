package com.wellnr.schooltrip.core.model.user;

import com.wellnr.ddd.DomainException;

public class PasswordsNotEqualException extends DomainException {

    private PasswordsNotEqualException() {
        super("The provided passwords are not equal.");
    }

    public static PasswordsNotEqualException apply() {
        return new PasswordsNotEqualException();
    }

}
