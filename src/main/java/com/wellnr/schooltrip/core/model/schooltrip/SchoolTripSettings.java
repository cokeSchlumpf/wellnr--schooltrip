package com.wellnr.schooltrip.core.model.schooltrip;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class SchoolTripSettings {

    double basePrice;
    double skiRentalPrice;
    double skiBootsRentalPrice;
    double snowboardRentalPrice;
    double snowboardBootsRentalPrice;
    double helmetRentalPrice;
    double tShirtPrice;

    String initialPaymentUrl;
    String remainingPaymentUrl;
    String completePaymentUrl;

    LocalDateTime registrationOpenUntil;

    public static SchoolTripSettings apply() {
        return apply(450, 60, 10, 65, 15, 5, 20, "", "", "", LocalDateTime.of(2023, 12, 16, 0, 0));
    }

}
