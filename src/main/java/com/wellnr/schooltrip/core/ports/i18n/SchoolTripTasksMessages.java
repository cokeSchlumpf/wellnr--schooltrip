package com.wellnr.schooltrip.core.ports.i18n;

public interface SchoolTripTasksMessages {

    default String importStudents() {
        return "Import students";
    }

    default String importStudentsDescription() {
        return "Import students from an Excel file.";
    }

    default String invitationMailing() {
        return "Invitation mailing";
    }

    default String invitationMailingDescription() {
        return "Exports data for invitation mailing.";
    }

    default String closeRegistration() {
        return "Close registration";
    }

    default String closeRegistrationDescription() {
        return "Close registration and remove studens who are not registered.";
    }

    default String reassignIDs() {
        return "(Re-)Assign IDs";
    }

    default String reassignIDsDescription() {
        return "Re-assign increasing ID to registered students.";
    }

}
