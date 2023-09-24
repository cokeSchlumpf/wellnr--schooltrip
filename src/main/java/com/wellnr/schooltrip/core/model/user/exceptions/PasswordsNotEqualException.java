package com.wellnr.schooltrip.core.model.user.exceptions;

import com.wellnr.ddd.DomainException;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;

public class PasswordsNotEqualException extends DomainException {

    private PasswordsNotEqualException() {
        super("The provided passwords are not equal.");
    }

    public static PasswordsNotEqualException apply() {
        return new PasswordsNotEqualException();
    }

    @Override
    public String getUserMessage(SchoolTripMessages i18n) {
        return i18n.passwordsNotEqual();
    }
}
