package com.wellnr.schooltrip.core.model.student;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class EmailConfirmationData {


    /**
     * The link which need to be visited to confirm the registration.
     */
    String confirmationUrl;

    /**
     * The name of the student as mentioned in the mail.
     */
    String studentName;

    boolean isSki;

    boolean isSnowboard;

    String experience;

    boolean isSkiRental;

    boolean isSnowboardRental;

    boolean isBootRental;

    boolean isHelmetRental;

}
