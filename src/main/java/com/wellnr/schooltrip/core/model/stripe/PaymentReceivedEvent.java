package com.wellnr.schooltrip.core.model.stripe;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@AllArgsConstructor(staticName = "apply")
public class PaymentReceivedEvent {

    String paymentToken;

    Double amount;

    LocalDate created;

}
