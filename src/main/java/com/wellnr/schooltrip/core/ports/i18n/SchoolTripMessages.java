package com.wellnr.schooltrip.core.ports.i18n;

import com.wellnr.common.markup.Either;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.questionaire.Experience;
import com.wellnr.schooltrip.core.model.student.questionaire.Questionnaire;
import com.wellnr.schooltrip.core.model.student.questionaire.Ski;
import com.wellnr.schooltrip.core.model.student.questionaire.Snowboard;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @DE("Weitere Informationen")
    default String additionalInformation() {
        return "Additional infomrmation";
    }

    @DE("Gibt es etwas abseits der Piste zu beachten?")
    default String additionalInformationInfo() {
        return "Furthermore we need some infomration regarding nutrition, and if there is something else we need to " +
            "know.";
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

    @DE("Zurück zur Registrierung")
    default String backToRegistration() {
        return "Back to registration";
    }

    @DE("Grundpreis")
    default String basePrice() {
        return "Base price";
    }

    @DE("Anfänger")
    default String beginner() {
        return "Beginner";
    }

    @DE("Anfänger - Keine, oder fast keine Erfahrung.")
    default String beginnerWithDesc() {
        return "Beginner - None or almost no experience.";
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

    default List<String> commonEmailContent(
        SchoolTripMessages i18n, SchoolTrip trip, Student student) {

        var questionnaire = student.getQuestionnaire().orElse(Questionnaire.empty());
        var parts = new ArrayList<String>();

        /*
         * Discipline Selection
         */
        var rental = questionnaire
            .getDisziplin()
            .getRental()
            .map(r -> {
                var weight = r.getWeight() + "Kg";
                var size = r.getHeight() + "cm";

                return i18n.yes() + " (" + weight + ", " + size + ")";
            })
            .orElse(i18n.no());

        var bootRental = questionnaire
            .getDisziplin()
            .getBootRental()
            .map(r -> {
                var size = r.getSize();

                return i18n.yes() + " (" + i18n.shoeSize() + " " + size + ")";
            })
            .orElse(i18n.no());

        if (questionnaire.getDisziplin() instanceof Ski ski) {
            parts.add(
                """
                    %s would like to do skiing.
                        - Experience: %s
                        - Ski rental: %s
                        - Ski boot rental: %s
                        - Helmet rental: %s
                    """
                    .stripIndent()
                    .formatted(
                        student.getFirstName(),
                        experience(i18n, ski.getExperience()),
                        rental,
                        bootRental,
                        questionnaire.getDisziplin().hasHelmRental() ? i18n.yes() : i18n.no()
                    )
            );
        } else if (questionnaire.getDisziplin() instanceof Snowboard snowboard) {
            parts.add(
                """
                    %s wants to do Boarding.
                        - Experience: %s
                        - Snowboard rental: %s
                        - Snowboard boots rental: %s
                        - Helmet rental: %s
                    """
                    .stripIndent()
                    .formatted(
                        student.getFirstName(),
                        experience(i18n, snowboard.getExperience()),
                        rental,
                        bootRental,
                        questionnaire.getDisziplin().hasHelmRental() ? i18n.yes() : i18n.no()
                    )
            );
        }

        /*
         * Additional information
         */
        var additionalInformation = new ArrayList<String>();

        var nutritionNotes = new ArrayList<String>();
        if (questionnaire.getNutrition().isHalal()) nutritionNotes.add(i18n.nutritionHalal());
        if (questionnaire.getNutrition().isVegetarian()) nutritionNotes.add(i18n.nutritionVegetarian());

        if (!nutritionNotes.isEmpty()) {
            additionalInformation.add("- %s eats %s".formatted(
                student.getFirstName(), String.join(" and ", nutritionNotes)
            ));
        }

        if (questionnaire.getComment().length() > 0) {
            additionalInformation.add("- Additional information for us:\n" + (questionnaire.getComment()
                .indent(6)));
        }

        if (!additionalInformation.isEmpty()) {
            parts.add(
                """
                    Something you told us:
                    %s
                    """
                    .stripIndent()
                    .formatted(
                        String.join("\n", additionalInformation).indent(3)
                    )
            );
        }

        /*
         * Pricing
         */
        var format = new DecimalFormat(i18n.currencyNumberFormat());

        var priceLineItems = student.getPriceLineItems(Either.fromRight(trip), i18n);
        if (priceLineItems.isPresent()) {
            var lineItems = priceLineItems
                .get()
                .getItems()
                .stream()
                .map(item -> "- " + item.label() + ": " + format.format(item.amount()) + " €")
                .collect(Collectors.joining("\n"));

            lineItems = lineItems + "\n- " + i18n.amountSum().toUpperCase() + ": " + priceLineItems.get()
                .getSumFormatted();
            lineItems = lineItems.indent(3);

            parts.add(
                """
                    Costs are calculated as follows:
                    %s
                    """
                    .stripIndent()
                    .formatted(lineItems)
            );
        }

        parts.add(
            """
                We are looking forward!
                Team of Ski- und Snowboard-Freizeit 2024
                """
        );

        return parts;
    }

    default List<String> commonEmailContent$DE(
        SchoolTripMessages i18n, SchoolTrip trip, Student student) {

        var questionnaire = student.getQuestionnaire().orElse(Questionnaire.empty());
        var parts = new ArrayList<String>();

        /*
         * Discipline Selection
         */
        var rental = questionnaire
            .getDisziplin()
            .getRental()
            .map(r -> {
                var weight = r.getWeight() + "Kg";
                var size = r.getHeight() + "cm";

                return i18n.yes() + " (" + weight + ", " + size + ")";
            })
            .orElse(i18n.no());

        var bootRental = questionnaire
            .getDisziplin()
            .getBootRental()
            .map(r -> {
                var size = r.getSize();

                return i18n.yes() + " (" + i18n.shoeSize() + " " + size + ")";
            })
            .orElse(i18n.no());

        if (questionnaire.getDisziplin() instanceof Ski ski) {
            parts.add(
                """
                    %s möchte Ski fahren.
                        - Erfahrung: %s
                        - Ski-Ausleihe gewünscht: %s
                        - Ski-Schuh-Aushleihe gewünscht: %s
                        - Helm-Ausleihe gewünscht: %s
                    """
                    .stripIndent()
                    .formatted(
                        student.getFirstName(),
                        experience(i18n, ski.getExperience()),
                        rental,
                        bootRental,
                        questionnaire.getDisziplin().hasHelmRental() ? i18n.yes() : i18n.no()
                    )
            );
        } else if (questionnaire.getDisziplin() instanceof Snowboard snowboard) {
            parts.add(
                """
                    %s möchte Snowboard fahren.
                        - Erfahrung: %s
                        - Snowboard-Ausleihe gewünscht: %s
                        - Snowboard-Boots-Aushleihe gewünscht: %s
                        - Helm-Ausleihe gewünscht: %s
                    """
                    .stripIndent()
                    .formatted(
                        student.getFirstName(),
                        experience(i18n, snowboard.getExperience()),
                        rental,
                        bootRental,
                        questionnaire.getDisziplin().hasHelmRental() ? i18n.yes() : i18n.no()
                    )
            );
        }

        /*
         * Additional information
         */
        var additionalInformation = new ArrayList<String>();

        var nutritionNotes = new ArrayList<String>();
        if (questionnaire.getNutrition().isHalal()) nutritionNotes.add(i18n.nutritionHalal());
        if (questionnaire.getNutrition().isVegetarian()) nutritionNotes.add(i18n.nutritionVegetarian());

        if (!nutritionNotes.isEmpty()) {
            additionalInformation.add("- %s ernärht sich %s".formatted(
                student.getFirstName(), String.join(" und ", nutritionNotes)
            ));
        }

        if (questionnaire.getComment().length() > 0) {
            additionalInformation.add("- Zusätzliche Informationen für uns:\n" + (questionnaire.getComment()
                .indent(6)));
        }

        if (!additionalInformation.isEmpty()) {
            parts.add(
                """
                    Weitere Informationen, die Sie uns mitgegeben haben:
                    %s
                    """
                    .stripIndent()
                    .formatted(
                        String.join("\n", additionalInformation).indent(3)
                    )
            );
        }

        /*
         * Pricing
         */
        var format = new DecimalFormat(i18n.currencyNumberFormat());

        var priceLineItems = student.getPriceLineItems(Either.fromRight(trip), i18n);
        if (priceLineItems.isPresent()) {
            var lineItems = priceLineItems
                .get()
                .getItems()
                .stream()
                .map(item -> "- " + item.label() + ": " + format.format(item.amount()) + " €")
                .collect(Collectors.joining("\n"));

            lineItems = lineItems + "\n- " + i18n.amountSum().toUpperCase() + ": " + priceLineItems.get()
                .getSumFormatted();
            lineItems = lineItems.indent(3);

            parts.add(
                """
                    Die Kosten für die Ausfahrt setzen sich wie folgt zusammen:
                    %s
                    """
                    .stripIndent()
                    .formatted(lineItems)
            );
        }

        parts.add(
            """
                Wir freuen uns auf %s! Viele Grüße!
                Das Team der Ski- und Snowboard-Freizeit 2024
                """
                .formatted(student.getFirstName())
        );

        return parts;
    }

    @DE("Anmeldung bestätigen")
    default String confirmRegistration() {
        return "Confirm registration";
    }

    @DE("Bestätigung")
    default String confirmation() {
        return "Confirmation";
    }

    @DE("""
        Bitte verifizieren Sie die verbindliche Anmeldung. Sie erhalten nach Absenden eine E-Mail zur Bestätigung der Anmeldung. Die Angaben zu Sportart, Ausleihe, Körpergröße und Gewicht können bei Bedarf zu einem späteren Zeitpunkt angepasst werden.
        """)
    default String confirmationInfo() {
        return "Please confirm the registration information by providing your email address.";
    }

    default String confirmationMailSubject(SchoolTrip schoolTrip) {
        return schoolTrip.getTitle() + " - Registration";
    }

    default String confirmationMailSubject$DE(SchoolTrip schoolTrip) {
        return schoolTrip.getTitle() + " - Anmeldung";
    }

    @DE("Kosten")
    default String costs() {
        return "Costs";
    }

    @DE("Basierend auf den gewählten Optionen berechnen sich die Kosten für die Fahrt wie folgt:")
    default String costsInfo() {
        return "Based on the current selection of options above, the following costs will be charged for the trip.";
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

    @DE("Ein, oder zwei Bretter?")
    default String disciplineSelection() {
        return "One or two boards?";
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

    default String experience(SchoolTripMessages i18n, Experience exp) {
        return switch (exp) {
            case BEGINNER -> i18n.beginner();
            case INTERMEDIATE -> i18n.intermediate();
            default -> i18n.expert();
        };
    }

    @DE("Profi")
    default String expert() {
        return "Expert";
    }

    @DE("Profi - Kann Berge sportlich und zügig abfahren. Hat Erfahrung aus mehreren Winter-Urlauben.")
    default String expertWithDesc() {
        return "Expert - Already has a lot of experience, can go in a sportive and safe style.";
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

    @DE("Auf der Piste besteht für alle Teilnehmer Helmpflicht.")
    default String helmetRentalInfo() {
        return "Everyone is required to wear a helmet on the pist.";
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

    @DE("Fortgeschritten - Erste Erfahrung gesammelt. Kann Berge sicher herunter fahren.")
    default String intermediateWithDesc() {
        return "Intermediate - Some experience, can go down the hill on his own in a safe driving style.";
    }

    default String internalServerError() {
        return "Internal server error";
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

    @DE("Wir haben die Möglichkeit Materialen günstig vor Ort auszuleihen. Gerne können aber auch eigene Geräte mit " +
        "gebracht und genutzt werden.")
    default String materialRentalInfo() {
        return "Please indicate whether materials should be rented by us at Hinterglemm, or own materials should be " +
            "used.";
    }

    default String myChildWantsToBoard(Student student) {
        return "%s wants to do Snowboard".formatted(student.getFirstName());
    }

    default String myChildWantsToBoard$DE(Student student) {
        return "%s möchte Snowboard fahren".formatted(student.getFirstName());
    }

    default String myChildWantsToSki(Student student) {
        return "%s wants to do Skiing".formatted(student.getFirstName());
    }

    default String myChildWantsToSki$DE(Student student) {
        return "%s möchte Ski fahren".formatted(student.getFirstName());
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

    @DE("Nein")
    default String no() {
        return "No";
    }

    @DE("Keine Präferenz")
    default String noPreference() {
        return "No preference";
    }

    @DE("Leider nicht authorisiert.")
    default String notAuthorized() {
        return "You're not authorized.";
    }

    @DE("Nicht erforderlich")
    default String notRequired() {
        return "Not required";
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

    @DE("Die Passwörter stimmen nicht überein.")
    default String passwordsNotEqual() {
        return "The passwords do not match.";
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

    default String registerStudentHeadline(Student student) {
        return "Registration for " + student.getDisplayName();
    }

    default String registerStudentHeadline$DE(Student student) {
        return "Anmeldung für " + student.getDisplayName();
    }

    default String registerStudentInfo(Student student) {
        return "Please fill out the registration form for " + student.getDisplayName() + " .";
    }

    default String registerStudentInfo$DE(Student student) {
        var msg = """
            Bitte nutzen Sie das folgende Formular, um %s für die Ski- und Snowboard-Lehrfahrt anzumelden.
            Falls Ihr Kind nicht an der Ski- und Snowboard-Lehrfahrt teilnimmt, melden Sie es bitte für die Ausfahrt "Out of Snow" an, oder bestätigen Sie, dass %s am Schulunterricht teilnimmt.
            <p>
            Gerne können Sie die Anmeldung bzw. Rückmeldung auch schriftlich über Ihr ausgehändigtes Formular bei Frau Wellner oder Herrn Ortinau abgeben.
            """
            .stripIndent();

        return String.format(msg, student.getDisplayName(), student.getDisplayName());
    }

    @DE("Anmeldung")
    default String registration() {
        return "Registration";
    }

    default String registrationConfirmationMailText(
        SchoolTripMessages i18n, SchoolTrip trip, Student student,
        String confirmationUrl, String updateUrl) {

        var parts = new ArrayList<String>();

        parts.add(
            """
                Registration Ski- und Snowboard-Freizeit 2024
                            
                We received the registration for %s. Please confirm the registration by following the link below:
                            
                %s
                            
                The registration is only complete after visiting the link.
                                
                You may change some of the registration options, to do so, follow the link below:
                                
                %s
                                
                Below you find the registered options.
                            
                """
                .stripIndent()
                .formatted(
                    student.getFirstName(),
                    confirmationUrl,
                    updateUrl
                )
        );

        parts.addAll(commonEmailContent(i18n, trip, student));
        return parts.stream().map(String::trim).collect(Collectors.joining("\n\n"));
    }

    default String registrationConfirmationMailText$DE(
        SchoolTripMessages i18n, SchoolTrip trip, Student student,
        String confirmationUrl, String updateUrl) {

        var parts = new ArrayList<String>();

        parts.add(
            """
                Anmeldung zur Ski- und Snowboard-Freizeit 2024
                            
                Die Anmeldung für %s für die Ski- und Snowboard-Freizeit haben wir entgegenegenommen. Bitte validieren Sie die Anmeldung in dem Sie den folgenden Link besuchen:
                            
                %s
                            
                Erst nach Besuch des Links, ist die Anmeldung vollständig abgeschlossen.
                                
                Einige Angaben können Sie bis eine Woche vor der Ausfahrt bei Bedarf anpassen. Nutzen Sie dafür den folgenden Link:
                                
                %s
                                
                Im folgenden finden Sie die registrierten Angaben.
                            
                """
                .stripIndent()
                .formatted(
                    student.getFirstName(),
                    confirmationUrl,
                    updateUrl
                )
        );

        parts.addAll(commonEmailContent(i18n, trip, student));
        return parts.stream().map(String::trim).collect(Collectors.joining("\n\n"));
    }

    @DE("Anmeldung offen bis")
    default String registrationOpenUntil() {
        return "Registration open until";
    }

    default String registrationUpdatedMailText(
        SchoolTripMessages i18n, SchoolTrip trip,
        Student student, String updateUrl) {

        var parts = new ArrayList<String>();

        parts.add(
            """
                Registration for Ski- und Snowboard-Freizeit 2024
                            
                The registration of %s has been updated.
                
                If you wish to change some of the registration options again, please follow the link below:
                                
                %s
                """
                .stripIndent()
                .formatted(
                    student.getFirstName(),
                    updateUrl
                )
        );

        parts.addAll(commonEmailContent(i18n, trip, student));
        return parts.stream().map(String::trim).collect(Collectors.joining("\n\n"));
    }

    default String registrationUpdatedMailText$DE(
        SchoolTripMessages i18n, SchoolTrip trip,
        Student student, String updateUrl) {

        var parts = new ArrayList<String>();

        parts.add(
            """
                Anmeldung zur Ski- und Snowboard-Freizeit 2024
                            
                Die Anmeldung für %s für die Ski- und Snowboard-Freizeit wurde aktualisiert. Im folgenden finden Sie die aktualisierten Angaben.
                                
                Sollten Sie die Angaben erneut anpassen wollen, nutzen Sie den folgenden Link:
                                
                %s
                """
                .stripIndent()
                .formatted(
                    student.getFirstName(),
                    updateUrl
                )
        );

        parts.addAll(commonEmailContent(i18n, trip, student));
        return parts.stream().map(String::trim).collect(Collectors.joining("\n\n"));
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

    @DE("Eine Klasse mit diesem Namen existiert bereits.")
    default String schoolClassAlreadyExists() {
        return "A school class with this name already exists.";
    }

    default String schoolClassNotFound(String schoolClass) {
        return "No school class `%s` found.".formatted(schoolClass);
    }

    default String schoolClassNotFound$DE(String schoolClass) {
        return "Es existiert keine Klasse `%s`".formatted(schoolClass);
    }

    @DE("Klassen")
    default String schoolClasses() {
        return "School classes";
    }

    @DE("Fahrt")
    default String schoolTrip() {
        return "School Trip";
    }

    @DE("Eine Klasse mit diesem Namen existiert bereits.")
    default String schoolTripAlreadyExists(String name) {
        return "A school trip with this name already exists.";
    }

    @DE("Fahrt Administratoren")
    default String schoolTripManagers() {
        return "Trip Managers";
    }

    default String schoolTripNotFound(String name) {
        return "No school trip `%s` found.".formatted(name);
    }

    default String schoolTripNotFound$DE(String name) {
        return "Keine Fahrt `%s` gefunden.".formatted(name);
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

    @DE("Ausleihe Ski")
    default String skiRental() {
        return "Ski rental";
    }

    @DE("Leihgebühr Ski")
    default String skiRentalPrice() {
        return "Ski rental price";
    }

    @DE("Snowboard")
    default String snowboard() {
        return "Snowboard";
    }

    @DE("Ausleihe Snowboard-Boots")
    default String snowboardBootRental() {
        return "Snowboard boot rental";
    }

    @DE("Leihgebühr Snowboard-Boots")
    default String snowboardBootsRentalPrice() {
        return "Snowboard boots rental price";
    }

    @DE("Ausleihe Snowboard")
    default String snowboardRental() {
        return "Snowboard rental";
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

    @DE("Schüler existiert bereits.")
    default String studentAlreadyExists() {
        return "Student already exists.";
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

    default String studentRegisteredText(Student student, String emailAddress) {
        return """
            We've sent an confirmation mail to %s, please check your mailbox and visit the link mentioned in the page.
            """
            .trim()
            .stripIndent()
            .formatted(emailAddress);
    }

    default String studentRegisteredText$DE(Student student, String emailAddress) {
        return
            """
                Wir haben eine E-Mail an %s gesendet. Bitte prüfen Sie ihren Posteingang und besuchen Sie den Bestätigungs-Link aus der E-Mail.
                """
                .trim()
                .stripIndent()
                .formatted(emailAddress);
    }

    default String studentRegisteredTitle(Student student) {
        return "Please confirm registration for %s".formatted(student.getFirstName());
    }

    default String studentRegisteredTitle$DE(Student student) {
        return "Bitte bestätigen Sie die Registrierung für %s".formatted(student.getFirstName());
    }

    default String studentRegisteredViewHeadline(Student student) {
        return """
            %s has been successfully registered for the trip
            """
            .formatted(student.getFirstName())
            .trim()
            .stripIndent();
    }

    default String studentRegisteredViewHeadline$DE(Student student) {
        return """
            %s wurde erfolgreich für den Skikurs angemeldet
            """
            .formatted(student.getFirstName())
            .trim()
            .stripIndent();
    }

    default String studentRegisteredViewInfo(Student student) {
        return """
            You may change rental settings or make payments.
            """
            .trim()
            .stripIndent();
    }

    default String studentRegisteredViewInfo$DE(Student student) {
        return """
            Sie können die Daten zur Ausleihe bis eine Woche vor Beginn des Skikurses anpassen oder Zahlungen durchführen.
            """
            .trim()
            .stripIndent()
            .formatted(student.getFirstName());
    }

    @DE("Link zur Selbst-Anmeldung")
    default String studentRegistrationLink() {
        return "Student Registration Link";
    }

    @DE("Mein Kind möchte teilnehmen:")
    default String studentWantsToParticipate() {
        return "My child wants to participate.";
    }

    @DE("Mein Kind benötigt einen Helm.")
    default String studentWantsToRentHelmet() {
        return "My child want to rent a helmet.";
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

    default String studentsExperienceLevelInfo(Student student) {
        return """
            Please indicate the experience of %s.
            """
            .stripIndent()
            .formatted(student.getDisplayName());
    }

    default String studentsExperienceLevelInfo$DE(Student student) {
        return """
            Bitte geben Sie an, wie erfahren %s in der gewählten Sportart ist.
            """
            .stripIndent()
            .formatted(student.getDisplayName());
    }

    @DE("Mein Kind nutzt die eigenen Ski-Schuhe.")
    default String studentsWantsToUseOwnSkiBoots() {
        return "My child wants to use own ski boots.";
    }

    @DE("Mein Kind nutzt die eigenen Snowboard-Boots.")
    default String studentsWantsToUseOwnSnowboardBoots() {
        return "My child wants to use own snowboard boots.";
    }

    @DE("Absenden")
    default String submit() {
        return "Submit";
    }

    @DE("English version")
    default String switchLanguage() {
        return "Deutsche Version";
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

    default String tokenNotFound(String token) {
        return "Token `%s` not found.".formatted(token);
    }

    default String tokenNotFound$DE(String token) {
        return "Token `%s` nicht gefunden.".formatted(token);
    }

    @DE("Typ")
    default String type() {
        return "Type";
    }

    @DE("Registrierung ansehen/ bearbeiten")
    default String updateRegistration() {
        return "View/ Update Registration";
    }

    default String updateRegistrationDescription(Student student) {
        return "Use the following form to update registration details for " + student.getFirstName() + ".";
    }

    default String updateRegistrationDescription$DE(Student student) {
        return "Nutzen Sie das folgende Formular, um die Anmeldedaten für " + student.getFirstName() + " anzupassen.";
    }

    default String updateRegistrationHeadline(Student student) {
        return "Update registration details for " + student.getFirstName();
    }

    default String updateRegistrationHeadline$DE(Student student) {
        return "Anmeldung anpassen für " + student.getFirstName();
    }

    @DE("Benutzer existiert bereits.")
    default String userAlreadyExists() {
        return "User already exists.";
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

    @DE("Ja")
    default String yes() {
        return "Yes";
    }

}
