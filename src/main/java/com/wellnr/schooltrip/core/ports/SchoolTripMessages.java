package com.wellnr.schooltrip.core.ports;

public interface SchoolTripMessages {

    default String login() {
        return "Login";
    }

    default String password() {
        return "Password";
    }

    default String username() {
        return "Username";
    }

    default String loginFailed() {
        return "Check that you have entered the correct username and password and try again.";
    }

    default String loginSucceeded() {
        return "Login succeeded.";
    }

    default String incorrectUsernameOrPassword() {
        return "Incorrect username or password";
    }

}
