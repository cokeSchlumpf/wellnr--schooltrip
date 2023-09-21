package com.wellnr.schooltrip.core.model.student.exceptions;

import com.wellnr.ddd.DomainException;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;

public class TokenNotFoundException extends DomainException {

    private final String token;

    public TokenNotFoundException(String token, String summary) {
        super(summary);
        this.token = token;
    }

    public static TokenNotFoundException apply(String token) {
        return new TokenNotFoundException(token, "The token `" + token + "` was not found.");
    }

    @Override
    public String getUserMessage(SchoolTripMessages i18n) {
        return i18n.tokenNotFound(token);
    }
}
