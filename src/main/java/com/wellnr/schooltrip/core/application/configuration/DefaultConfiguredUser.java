package com.wellnr.schooltrip.core.application.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DefaultConfiguredUser {

    String email;

    String password;

    String firstName;

    String lastName;

}
