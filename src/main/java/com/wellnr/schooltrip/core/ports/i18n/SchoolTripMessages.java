package com.wellnr.schooltrip.core.ports.i18n;

import com.wellnr.schooltrip.core.model.student.Student;

import java.text.MessageFormat;

public interface SchoolTripMessages {

    @DE("Manager hinzufügen")
    default String addManager() {
        return "Add manager";
    }

    @DE("Benutzer hinzufügen")
    default String addRegisteredUser() {
        return "Add user";
    }

    @DE("Klasse hinzufügen")
    default String addSchoolClass() {
        return "Add school class";
    }

    @DE("Fahrt hinzufügen")
    default String addSchoolTrip() {
        return "Add school trip";
    }

    @DE("Schüler hinzufügen")
    default String addStudent() {
        return "Add student";
    }

    @DE("Alle Fahrten")
    default String allSchoolTrips() {
        return "All school trips";
    }

    @DE("Betrag")
    default String amount() {
        return "Amount";
    }

    @DE("Gesamt")
    default String amountSum() {
        return "Sum";
    }

    @DE("Anwendungs-Einstelltungen")
    default String applicationSettings() {
        return "Application settings";
    }

    @DE("< Zurück")
    default String backButton() {
        return "< Back";
    }

    @DE("Grundpreis")
    default String basePrice() {
        return "Base price";
    }

    @DE("Anfänger")
    default String beginner() {
        return "Beginner";
    }

    @DE("Geburtstag")
    default String birthday() {
        return "Birthday";
    }

    @DE("Körpergröße (in cm)")
    default String bodyHeight() {
        return "Bodysize (in cm)";
    }

    @DE("Körpergewicht (in KG)")
    default String bodyWeight() {
        return "Bodyweight (in KG)";
    }

    @DE("Ausleihe Schuhe")
    default String bootRental() {
        return "Boot Rental";
    }

    @DE("Registrierung beenden")
    default String closeRegistration() {
        return "Close registration";
    }

    @DE("Registrierung beenden; alle Schüler die nicht registriert sind werden aus der Schülerliste entfernt.")
    default String closeRegistrationDescription() {
        return "Close registration and remove studens who are not registered.";
    }

    @DE("Anmeldung bestätigen")
    default String confirmRegistration() {
        return "Confirm registration";
    }

    @DE("Bestätigung")
    default String confirmation() {
        return "Confirmation";
    }

    @DE("Bitte verifizieren Sie die verbindliche Anmeldung. Die Angaben zu Sportart, " +
        "Ausleihe, Körpergröße und -gewicht können noch später angepasst werden.")
    default String confirmationInfo() {
        return "Please confirm the registration information.";
    }

    @DE("Kosten")
    default String costs() {
        return "Costs";
    }

    default String costsInfo() {
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.";
    }

    @DE("Fahrt anlegen")
    default String createSchoolTrip() {
        return "Create trip";
    }

    @DE("Betrag")
    default String currencyAmount() {
        return "Amount";
    }

    default String currencyNumberFormat() {
        return "#.00";
    }

    @DE("Datum")
    default String date() {
        return "Date";
    }

    @DE("Geburtstadatum")
    default String dateOfBirth() {
        return "Date of Birth";
    }

    @DE("Beschreibung")
    default String description() {
        return "Description";
    }

    @DE("Differenz")
    default String diffAmount() {
        return "Diff";
    }

    @DE("Disziplin")
    default String discipline() {
        return "Discipline";
    }

    @DE("Disziplinen")
    default String disciplines() {
        return "Disciplines";
    }

    @DE("Klasse bearbeiten")
    default String editSchoolClass() {
        return "Edit school class";
    }

    @DE("Schüler bearbeiten")
    default String editStudent() {
        return "Edit student";
    }

    @DE("E-Mail")
    default String email() {
        return "Email";
    }

    @DE("E-Mail-Adresse")
    default String emailAddress() {
        return "Email address";
    }

    @DE("Englisch")
    default String english() {
        return "English";
    }

    @DE("Zahlungen eingeben")
    default String enterPayments() {
        return "Enter payments";
    }

    @DE("Zu zahlen")
    default String expectedAmount() {
        return "Expected";
    }

    @DE("Erfahrung")
    default String experience() {
        return "Experience";
    }

    @DE("Profi")
    default String expert() {
        return "Expert";
    }

    @DE("Datei enthält Tabellenüberschriften.")
    default String fileIncludesHeaderRow() {
        return "File includes header row.";
    }

    @DE("Vorname")
    default String firstName() {
        return "Firstname";
    }

    @DE("Geschlecht")
    default String gender() {
        return "Gender";
    }

    @DE("w")
    default String genderFemaleAbbreviation() {
        return "f";
    }

    @DE("m")
    default String genderMaleAbbreviation() {
        return "m";
    }

    @DE("Deutsch")
    default String german() {
        return "German";
    }

    @DE("Ausleihe Helm")
    default String helmetRental() {
        return "Helmet rental";
    }

    @DE("Daten importieren")
    default String importData() {
        return "Import data";
    }

    @DE("Excel Datei importtieren")
    default String importExcelFile() {
        return "Import Excel file";
    }

    @DE("Schülerliste importieren")
    default String importStudents() {
        return "Import students";
    }

    @DE("Schülerliste aus einer Excel-Datei importieren.")
    default String importStudentsDescription() {
        return "Import students from an Excel file.";
    }

    @DE("Benutzername oder Passwort nicht korrekt.")
    default String incorrectUsernameOrPassword() {
        return "Incorrect username or password";
    }

    @DE("Fortgeschritten")
    default String intermediate() {
        return "Fortgeschritten/ Erste Erfahrungen gesammelt.";
    }

    @DE("Einladungs-Schreiben")
    default String invitationMailing() {
        return "Invitation mailing";
    }

    @DE("Daten für die Erstellung des Einladungsschreibens exportieren.")
    default String invitationMailingDescription() {
        return "Exports data for invitation mailing.";
    }

    @DE("Position")
    default String invoicePosition() {
        return "Position";
    }

    @DE("Nachname")
    default String lastName() {
        return "Lastname";
    }

    @DE("Login")
    default String login() {
        return "Login";
    }

    @DE("Login fehlgeschlagen. Benutzername oder Passwort nicht korrekt.")
    default String loginFailed() {
        return "Check that you have entered the correct username and password and try again.";
    }

    @DE("Login erfolgreich.")
    default String loginSucceeded() {
        return "Login succeeded.";
    }

    @DE("Abmelden")
    default String logout() {
        return "Logout";
    }

    @DE("Material-Ausleihe")
    default String materialRental() {
        return "Rental Options";
    }

    // TODO
    default String materialRentalInfo() {
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.";
    }

    @DE("Name")
    default String name() {
        return "Name";
    }

    @DE("Neues Passwort")
    default String newPassword() {
        return "New password";
    }

    @DE("Weiter >")
    default String nextButton() {
        return "Next >";
    }

    @DE("Keine Präferenz")
    default String noPreference() {
        return "No preference";
    }

    @DE("Essgewohnheiten")
    default String nutrition() {
        return "Nutrition preferences";
    }

    @DE("Halal")
    default String nutritionHalal() {
        return "halal";
    }

    @DE("Vegetarisch")
    default String nutritionVegetarian() {
        return "vegetarisch";
    }

    @DE("Übersicht")
    default String overview() {
        return "Overview";
    }

    @DE("Gezahlt")
    default String paidAmount() {
        return "Paid";
    }

    @DE("Passwort")
    default String password() {
        return "Password";
    }

    @DE("Zahlungen")
    default String payments() {
        return "Payments";
    }

    @DE("Eingegangene Zahlungen")
    default String paymentsMade() {
        return "Payments made";
    }

    @DE("Sprache")
    default String preferredLocale() {
        return "Preferred locale";
    }

    @DE("Profil-Einstellungen")
    default String profileSettings() {
        return "Profile Settings";
    }

    @DE("IDs zuweisen")
    default String reassignIDs() {
        return "(Re-)Assign IDs";
    }

    @DE("Fortlaufende IDs an alle Schüler zuweisen.")
    default String reassignIDsDescription() {
        return "Re-assign increasing ID to registered students.";
    }

    @DE("Zahlung eingeben")
    default String recordPayment() {
        return "Record payment";
    }

    @DE("Anmeldung Ski-Kurs 2023") // TODO
    default String registerStudentHeadline(Student student) {
        return "Registration for Ski and Snowboard Days 2023 für " + student.getDisplayName();
    }

    @DE("Nutzen Sie das folgende Formular für die Registrierung Ihres Kindes.") // TODO
    default String registerStudentInfo(Student student) {
        return "Please fill out the registration form for " + student.getDisplayName() + " .";
    }

    @DE("Anmeldung")
    default String registration() {
        return "Registration";
    }

    // TODO
    default String registrationConfirmationEmailText(Student student) {
        return MessageFormat.format("""
            Danke für die Registrierung `{0}`!
                        
            Abschließen hier Kollege: http://localhost:8080/students/confirm-registration/{1}
            """, student.getDisplayName(), student.getConfirmationToken());
    }

    @DE("Anmeldung offen bis")
    default String registrationOpenUntil() {
        return "Registration open until";
    }

    @DE("Ausleihe Ski/ Board")
    default String rental() {
        return "Ski and snowboard rental";
    }

    @DE("Neues Passwort wiederholen")
    default String repeatNewPassword() {
        return "Repeat New Password";
    }

    @DE("Passwort zurücksetzen")
    default String resetPassword() {
        return "Reset Password";
    }

    @DE("Speichern")
    default String save() {
        return "Save";
    }

    @DE("Klasse")
    default String schoolClass() {
        return "School Class";
    }

    @DE("Klassen")
    default String schoolClasses() {
        return "School classes";
    }

    @DE("Fahrt")
    default String schoolTrip() {
        return "School Trip";
    }

    @DE("Fahrt Administratoren")
    default String schoolTripManagers() {
        return "Trip Managers";
    }

    @DE("Fahrt Einstellungen")
    default String schoolTripSettings() {
        return "Trip Settings";
    }

    @DE("Fahrten")
    default String schoolTrips() {
        return "School Trips";
    }

    @DE("Einstellungen")
    default String settings() {
        return "Settings";
    }

    @DE("Schuhgröße")
    default String shoeSize() {
        return "Shoe size";
    }

    @DE("Ski")
    default String ski() {
        return "Ski";
    }

    @DE("Ausleihe Ski-Schuhe")
    default String skiBootRental() {
        return "Ski Boot Rental";
    }

    @DE("Leihgebühr Ski-Schuhe")
    default String skiBootsRentalPrice() {
        return "Ski boots rental price";
    }

    @DE("Leihgebühr Ski")
    default String skiRentalPrice() {
        return "Ski rental price";
    }

    @DE("Snowboard")
    default String snowboard() {
        return "Snowboard";
    }

    @DE("Leihgebühr Snowboard-Boots")
    default String snowboardBootsRentalPrice() {
        return "Snowboard boots rental price";
    }

    @DE("Leihgebühr Snowboard")
    default String snowboardRentalPrice() {
        return "Snowboard rental price";
    }

    @DE("Status")
    default String status() {
        return "Status";
    }

    @DE("Schüler")
    default String student() {
        return "Student";
    }

    @DE("SchülerIn ist nicht angemeldet. Die Anmeldung kann manuell durchgeführt werden.")
    default String studentIsNotRegisteredYet() {
        return "Student is not registered yet. You may register the student manually.";
    }

    @DE("SchülerIn ist angemeldet. Die Anmeldedaten können angepasst werden.")
    default String studentIsRegistered() {
        return "Student has been registred. You may change the configurations of the student.";
    }

    @DE("Die Anmeldung wurde durchgeführt, jedoch wurde sie bisher nicht bestätigt.")
    default String studentIsWaitingForConfirmation() {
        return "Student has been registered, but confirmation link has not been called.";
    }

    @DE("Link zur Selbst-Anmeldung")
    default String studentRegistrationLink() {
        return "Student Registration Link";
    }

    @DE("Mein Kind möchte teilnehmen:")
    default String studentWantsToParticipate() {
        return "My child wants to participate.";
    }

    @DE("Mein Kind möchte Ski und Zubehör ausleihen.")
    default String studentWantsToRentSki() {
        return "My child wants to rent skie and assecories.";
    }

    @DE("Mein Kind möchte Ski-Schuhe ausleihen.")
    default String studentWantsToRentSkiBoots() {
        return "My child rents ski boots.";
    }

    @DE("Mein Kind möchte ein Snowboard ausleihen.")
    default String studentWantsToRentSnowboard() {
        return "My child wants to rent a snowboard.";
    }

    @DE("Mein Kind möchte Snowboard-Boots aushleihen.")
    default String studentWantsToRentSnowboardBoots() {
        return "My child wants to rent snowboard boots.";
    }

    @DE("Mein Kind möchte eiegne Ski mitbringen und nutzen.")
    default String studentWantsToUseOwnSki() {
        return "My child wants to use own ski.";
    }

    @DE("Mein Kind möchte eine eigenes Snowboard mitbringen und nutzen")
    default String studentWantsToUseOwnSnowboard() {
        return "My child wants to use his/ her own snowboard.";
    }

    @DE("SchülerInnen")
    default String students() {
        return "Students";
    }

    @DE("Erfahrungslevel des Kindes")
    default String studentsExperienceLevel() {
        return "Experience level of the child";
    }

    // TODO: Invent templates.
    default String studentsExperienceLevelInfo(Student student) {
        return "Bitte geben Sie an, wie erfahren " + student.getFirstName() + " in der gewählten " +
            "Sportart ist. Lorem " +
            "Ipsum, erklären was die Level bedeuten.";
    }

    @DE("Mein Kind nutzt die eigenen Ski-Schuhe.")
    default String studentsWantsToUseOwnSkiBoots() {
        return "My child wants to use own ski boots.";
    }

    @DE("Mein Kind nutzt die eigenen Snowboard-Boots.")
    default String studentsWantsToUseOwnSnowboardBoots() {
        return "My child wants to use own snowboard boots.";
    }

    @DE("Submit")
    default String submit() {
        return "Absenden";
    }

    @DE("Spalte")
    default String tableColumn() {
        return "column";
    }

    @DE("Aufgaben")
    default String tasks() {
        return "Tasks";
    }

    @DE("Titel")
    default String title() {
        return "Title";
    }

    @DE("Typ")
    default String type() {
        return "Type";
    }

    @DE("Benutzer-Profil")
    default String userProfile() {
        return "Profile";
    }

    @DE("Benutzer-Einstellungen")
    default String userProperties() {
        return "User Properties";
    }

    @DE("Benutzer-Rollen")
    default String userRoles() {
        return "roles";
    }

    @DE("Der Benutzer had Administrator-Rechte.")
    default String userShouldBeAdmin() {
        return "User should be administrator.";
    }

    @DE("Benutzername")
    default String username() {
        return "Username";
    }

}
