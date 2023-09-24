package com.wellnr.schooltrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
    "com.wellnr.schooltrip",
    "com.wellnr.ddd"
})
public class SchooltripApplication {

    public static String SECURITY_COOKIE_NAME = "access_token";

    public static void main(String[] args) {
        SpringApplication.run(SchooltripApplication.class, args);
    }

}
