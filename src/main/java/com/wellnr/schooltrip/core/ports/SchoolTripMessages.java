package com.wellnr.schooltrip.core.ports;

import com.wellnr.schooltrip.core.model.student.Student;

import java.text.MessageFormat;

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

    default String registrationConfirmationEmailText(Student student) {
        return MessageFormat.format("""
            Danke für die Registrierung `{0}`!
            
            Abschließen hier Kollege: http://localhost:8080/students/confirm-registration/{1} 
            """, student.getDisplayName(), student.getConfirmationToken());
    }

}
