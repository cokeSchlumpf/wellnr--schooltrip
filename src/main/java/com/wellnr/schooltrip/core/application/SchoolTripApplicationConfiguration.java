package com.wellnr.schooltrip.core.application;

import com.wellnr.schooltrip.core.application.configuration.DefaultConfiguredUser;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class SchoolTripApplicationConfiguration {

    List<DefaultConfiguredUser> defaultAdminUsers;

}
