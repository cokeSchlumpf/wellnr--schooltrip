package com.wellnr.schooltrip.core.ports.i18n;

import com.vaadin.flow.component.Component;
import com.wellnr.schooltrip.core.model.student.Student;

import java.text.MessageFormat;

public interface SchoolTripMessages extends SchoolTripTasksMessages {

    default String addManager() {
        return "Add manager";
    }

    default String addRegisteredUser() {
        return "Add user";
    }

    default String addSchoolClass() {
        return "Add school class";
    }

    default String addSchoolTrip() {
        return "Add school trip";
    }

    default String addStudent() {
        return "Add student";
    }

    default String allSchoolTrips() {
        return "All school trips";
    }

    default String applicationSettings() {
        return "Application settings";
    }

    default String backButton() {
        return "< Back";
    }

    default String basePrice() {
        return "Base price";
    }

    default String bootRental() {
        return "Ausleihe Schuhe";
    }

    default String currencyAmount() {
        return "Amount";
    }

    default String currencyNumberFormat() {
        return "#.00";
    }

    default String date() {
        return "Date";
    }

    default String description() {
        return "Description";
    }

    default String logout() {
        return "Logout";
    }

    default String paymentsMade() {
        return "Payments made";
    }

    default String recordPayment() {
        return "Record payment";
    }

    default String registration() {
        return "Registration";
    }

    default String student() {
        return "Student";
    }

    default String studentRegistrationLink() {
        return "Student Registration Link";
    }

    default String tableColumn() {
        return "column";
    }

    default String createSchoolTrip() {
        return "Create trip";
    }

    default String dateOfBirth() {
        return "Date of Birth";
    }

    default String diffAmount() {
        return "Diff";
    }

    default String discipline() {
        return "Discipline";
    }

    default String disciplines() { return "Disciplines"; }

    default String email() {
        return "Email";
    }

    default String enterPayments() {
        return "Enter payments";
    }

    default String expectedAmount() {
        return "Expected";
    }

    default String experience() {
        return "Experience";
    }

    default String fileIncludesHeaderRow() {
        return "File includes header row.";
    }

    default String firstName() {
        return "Firstname";
    }

    default String gender() {
        return "Gender";
    }

    default String genderFemaleAbbreviation() {
        return "w";
    }

    default String genderMaleAbbreviation() {
        return "m";
    }

    default String helmetRental() {
        return "Helmet rental";
    }

    default String importData() {
        return "Import data";
    }

    default String importExcelFile() {
        return "Import Excel file";
    }

    default String incorrectUsernameOrPassword() {
        return "Incorrect username or password";
    }

    default String lastName() {
        return "Lastname";
    }

    default String login() {
        return "Login";
    }

    default String loginFailed() {
        return "Check that you have entered the correct username and password and try again.";
    }

    default String loginSucceeded() {
        return "Login succeeded.";
    }

    default String name() {
        return "Name";
    }

    default String newPassword() {
        return "New password";
    }

    default String nextButton() {
        return "Next >";
    }

    default String overview() {
        return "Overview";
    }

    default String paidAmount() {
        return "Paid";
    }

    default String password() {
        return "Password";
    }

    default String payments() {
        return "Payments";
    }

    default String profileSettings() {
        return "Profile Settings";
    }

    default String registrationConfirmationEmailText(Student student) {
        return MessageFormat.format("""
            Danke für die Registrierung `{0}`!
                        
            Abschließen hier Kollege: http://localhost:8080/students/confirm-registration/{1}
            """, student.getDisplayName(), student.getConfirmationToken());
    }

    default String registrationOpenUntil() {
        return "Registration open until";
    }

    default String rental() {
        return "Ausleihe Ski/ Board";
    }

    default String repeatNewPassword() {
        return "Repeat New Password";
    }

    default String schoolClass() {
        return "School Class";
    }

    default String schoolClasses() {
        return "School classes";
    }

    default String schoolTrip() {
        return "School Trip";
    }

    default String schoolTripManagers() {
        return "Trip Managers";
    }

    ;

    default String schoolTripSettings() {
        return "Trip Settings";
    }

    default String schoolTrips() {
        return "School Trips";
    }

    default String settings() {
        return "Settings";
    }

    default String ski() {
        return "Ski";
    }

    default String skiBootsRentalPrice() {
        return "Ski boots rental price";
    }

    default String skiRentalPrice() {
        return "Ski rental price";
    }

    default String snowboardBootsRentalPrice() {
        return "Snowboard boots rental price";
    }

    default String snowboardRentalPrice() {
        return "Snowboard rental price";
    }

    default String students() {
        return "Students";
    }

    default String tasks() {
        return "Tasks";
    }

    default String title() {
        return "Title";
    }

    default String userProfile() {
        return "Profile";
    }

    default String userRoles() {
        return "roles";
    }

    default String userShouldBeAdmin() {
        return "User should be administrator.";
    }

    default String username() {
        return "Username";
    }

}
