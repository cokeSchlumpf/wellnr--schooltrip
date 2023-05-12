package com.wellnr.schooltrip;

import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.RegisterAdminUserCommand;
import com.wellnr.schooltrip.core.application.commands.CreateSchoolTripCommand;
import com.wellnr.schooltrip.core.application.commands.RegisterSchoolClassCommand;
import com.wellnr.schooltrip.core.application.commands.RegisterStudentCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.student.StudentsRepository;
import com.wellnr.schooltrip.core.model.user.AnonymousUser;
import com.wellnr.schooltrip.util.MongoContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

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

        /*
         * Register user for following actions.
         */
        RegisterAdminUserCommand
            .apply(
                "michael.wellner@gmail.com", "secret", "Michael", "Wellner"
            )
            .run(
                AnonymousUser.apply(), registry
            );

        var user = app
            .getBean(SchoolTripDomainRegistry.class)
            .getUsers()
            .getOneByEmail("michael.wellner@gmail.com");

        /*
         * Register new school trip.
         */
        CreateSchoolTripCommand
            .apply(
                "Skikurs 2024", "skikurs-2024"
            )
            .run(
                user, registry
            );

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
            "Olsen"
        ).run(user, registry);

        var trip = registry
            .getSchoolTrips()
            .getSchoolTripByName("skikurs-2024");

        var student = registry
            .getStudents()
            .getStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(
                new SchoolTripId(trip.getId()), "8a", "Egon", "Olsen"
            );

        System.out.println("http://localhost:8080/students/" + student.getToken());
    }

}
