package com.wellnr.schooltrip;

import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.schooltrip.CreateSchoolTripCommand;
import com.wellnr.schooltrip.core.application.commands.schooltrip.RegisterSchoolClassCommand;
import com.wellnr.schooltrip.core.application.commands.schooltrip.UpdateSchoolTripSettingsCommand;
import com.wellnr.schooltrip.core.application.commands.students.CompleteOrUpdateStudentRegistrationCommand;
import com.wellnr.schooltrip.core.application.commands.students.RegisterStudentCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.student.Gender;
import com.wellnr.schooltrip.core.model.student.questionaire.Experience;
import com.wellnr.schooltrip.core.model.student.questionaire.Questionnaire;
import com.wellnr.schooltrip.core.model.student.questionaire.Ski;
import com.wellnr.schooltrip.util.MongoContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDate;

@SpringBootApplication
@ComponentScan({
    "com.wellnr.schooltrip",
    "com.wellnr.ddd"
})
class SchooltripApplicationTests {

    public static void main(String... args) {
        var mongoContainer = MongoContainer.apply();
        mongoContainer.start();

        System.setProperty("spring.data.mongodb.host", "localhost");
        System.setProperty("spring.data.mongodb.port", "" + mongoContainer.getPort());
        System.setProperty("spring.data.mongodb.database", "app");
        System.setProperty("spring.data.mongodb.username", "app");
        System.setProperty("spring.data.mongodb.password", "password");

        var app = SpringApplication.run(SchooltripApplication.class, args);
        var registry = app.getBean(SchoolTripDomainRegistry.class);

        // Operators.suppressExceptions(() -> Thread.sleep(10000));

        /*
         * Register user for following actions.
         */
        var user = app
            .getBean(SchoolTripDomainRegistry.class)
            .getUsers()
            .getOneByEmail("michael.wellner@gmail.com");

        /*
         * Register new school trip.
         */
        var schoolTrip = CreateSchoolTripCommand
            .apply(
                "Skikurs 2024", "skikurs-2024"
            )
            .run(
                user, registry
            )
            .getData();

        UpdateSchoolTripSettingsCommand
            .apply(
                schoolTrip.getName(),
                schoolTrip
                    .getSettings()
                    .withInitialPaymentUrl(
                        "https://book.stripe.com/test_fZe5mD3688BN7aE3cc?client_reference_id=:paymentToken"
                    )
                    .withRemainingPaymentUrl(
                        "https://book.stripe.com/test_fZe5mD3688BN7aE3cc?client_reference_id=:paymentToken"
                    )
                    .withCompletePaymentUrl(
                        "https://book.stripe.com/test_fZe5mD3688BN7aE3cc?client_reference_id=:paymentToken"
                    )
            )
            .run(user, registry);

        RegisterSchoolClassCommand
            .apply(
                "skikurs-2024",
                "8a"
            )
            .run(
                user, registry
            );

        RegisterStudentCommand.apply(
            "skikurs-2024",
            "8a",
            "Egon",
            "Olsen",
            LocalDate.now(),
            Gender.Male
        ).run(user, registry);

        RegisterStudentCommand.apply(
            "skikurs-2024",
            "8a",
            "Edgar",
            "Wellner",
            LocalDate.of(2024, 9, 24),
            Gender.Male
        ).run(user, registry);

        var trip = registry
            .getSchoolTrips()
            .getSchoolTripByName("skikurs-2024");

        var egon = registry
            .getStudents()
            .getStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(
                new SchoolTripId(trip.getId()), "8a", "Egon", "Olsen"
            );

        var edgar = registry
            .getStudents()
            .getStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(
                new SchoolTripId(trip.getId()), "8a", "Edgar", "Wellner"
            );

        CompleteOrUpdateStudentRegistrationCommand
            .apply(
                edgar.getToken(), Questionnaire.empty().withDisziplin(Ski.apply(Experience.EXPERT)), "michael.wellner@gmail.com"
            )
            .run(user, registry);

        edgar = registry
            .getStudents()
            .getStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(
                new SchoolTripId(trip.getId()), "8a", "Edgar", "Wellner"
            );

        System.out.println("http://localhost:8080/students/response/" + egon.getToken());
        System.out.println("http://localhost:8080/students/confirm-registration/" + edgar.getConfirmationToken());

        for (var i = 0; i < 10; i++) {
            var schoolClass = "8a";
            var firstName = "Test FirstName " + i;
            var lastName = "Test LastName " + i;

            RegisterStudentCommand.apply(
                "skikurs-2024",
                schoolClass,
                firstName,
                lastName,
                LocalDate.now(),
                Gender.Male
            ).run(user, registry);

            var student = registry
                .getStudents()
                .getStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(
                    new SchoolTripId(trip.getId()), schoolClass, firstName, lastName
                );

            if (i % 2 == 0) {
                student.completeOrUpdateStudentRegistrationByOrganizer(
                    Questionnaire.fakeSki(), registry.getStudents()
                );
            } else {
                student.completeOrUpdateStudentRegistrationByOrganizer(
                    Questionnaire.empty(), registry.getStudents()
                );
            }

            student.confirmStudentRegistration(registry.getStudents());
        }

        for (var i = 10; i < 17; i++) {
            var schoolClass = "8a";
            var firstName = "Test FirstName " + i;
            var lastName = "Test LastName " + i;

            RegisterStudentCommand.apply(
                "skikurs-2024",
                schoolClass,
                firstName,
                lastName,
                LocalDate.now(),
                Gender.Male
            ).run(user, registry);

            var student = registry
                .getStudents()
                .getStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(
                    new SchoolTripId(trip.getId()), schoolClass, firstName, lastName
                );

            student.completeOrUpdateStudentRegistrationByOrganizer(
                Questionnaire.fakeSnowboard(), registry.getStudents()
            );

            student.confirmStudentRegistration(registry.getStudents());
        }
    }

}
