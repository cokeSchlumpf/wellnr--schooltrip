package com.wellnr.schooltrip.infrastructure;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@VaadinSessionScope
public class UserSession {

    RegisteredUser user;

    public UserSession() {

    }

    public Optional<RegisteredUser> getRegisteredUser() {
        return Optional.ofNullable(user);
    }

    public void setRegisteredUser(RegisteredUser user) {
        this.user = user;
    }
}
