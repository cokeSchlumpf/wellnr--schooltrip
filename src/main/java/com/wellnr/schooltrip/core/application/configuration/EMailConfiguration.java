package com.wellnr.schooltrip.core.application.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class EMailConfiguration {

    /**
     * The mode in which the E-Mail client should be configured.
     * Allowed values are "fake" and "smtp".
     */
    String mode;

    /**
     * The host of the E-Mail (SMTP) server.
     */
    String host;

    /**
     * The port of the E-Mail (SMTP) server.
     */
    int port;

    /**
     * The username of the E-Mail account.
     */
    String username;

    /**
     * The password of the E-Mail account.
     */
    String password;

    /**
     * Additional properties to be used when connecting to the E-Mail server.
     */
    Map<String, String> properties;

}
