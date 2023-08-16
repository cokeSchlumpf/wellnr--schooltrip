package com.wellnr.schooltrip.core.model.student.payments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment implements Transaction {

    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String TYPE = "type";
    private static final String DESCRIPTION = "description";
    private static final String AMOUNT = "amount";

    @JsonProperty(ID)
    String id;

    @JsonProperty(DATE)
    LocalDate date;

    @JsonProperty(TYPE)
    PaymentType type;

    @JsonProperty(DESCRIPTION)
    String description;

    @JsonProperty(AMOUNT)
    double amount;

    @JsonCreator
    public static Payment apply(
        @JsonProperty(ID) String id,
        @JsonProperty(DATE) LocalDate date,
        @JsonProperty(TYPE) PaymentType type,
        @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(AMOUNT) double amount
    ) {
        return new Payment(id, date, type, description, amount);
    }

    public static Payment createNew(
        LocalDate date,
        PaymentType type,
        String description,
        double amount
    ) {
        var id = UUID.randomUUID().toString();
        return Payment.apply(id, date, type, description, amount);
    }

}
