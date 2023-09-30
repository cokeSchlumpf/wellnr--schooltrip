package com.wellnr.schooltrip.core.application;

import com.wellnr.schooltrip.core.application.configuration.DefaultConfiguredUser;
import com.wellnr.schooltrip.core.application.configuration.EMailConfiguration;
import com.wellnr.schooltrip.core.application.configuration.StripeConfiguration;
import com.wellnr.schooltrip.core.application.configuration.UIConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class SchoolTripApplicationConfiguration {

    List<DefaultConfiguredUser> defaultAdminUsers;

    List<DefaultConfiguredUser> defaultTeachers;

    UIConfiguration ui;

    EMailConfiguration email;

    StripeConfiguration stripe;

}
