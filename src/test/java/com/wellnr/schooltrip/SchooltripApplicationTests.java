package com.wellnr.schooltrip;

import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.RegisterAdminUserCommand;
import com.wellnr.schooltrip.core.application.commands.CreateSchoolTripCommand;
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

        /*
         * Register user for following actions.
         */
        RegisterAdminUserCommand
            .apply(
                "michael.wellner@gmail.com", "secret", "Michael", "Wellner"
            )
            .run(
                AnonymousUser.apply(),
                app.getBean(SchoolTripDomainRegistry.class)
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
                user,
                app.getBean(SchoolTripDomainRegistry.class)
            );
    }

}
