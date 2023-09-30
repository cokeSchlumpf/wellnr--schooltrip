package com.wellnr.schooltrip.core.model.stripe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wellnr.ddd.AggregateRoot;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StripePayment extends AggregateRoot<String, StripePayment> {

    private static final String ID = "id";
    private static final String PAYMENT_TOKEN = "payment-token";
    private static final String CHECKOUT_SESSION_ID = "checkout-session-id";
    private static final String PAYMENT_INTENT_ID = "payment-intent-id";
    private static final String CLIENT_NAME = "client-name";
    private static final String CLIENT_EMAIL = "client-email";
    private static final String AMOUNT = "amount";
    private static final String CREATED = "created";
    private static final String EVENT_SENT = "event-sent";

    @JsonProperty(ID)
    String id;

    @JsonProperty(PAYMENT_TOKEN)
    String paymentToken;

    @JsonProperty(CHECKOUT_SESSION_ID)
    String checkoutSessionId;

    @JsonProperty(PAYMENT_INTENT_ID)
    String paymentIntentId;

    @JsonProperty(CLIENT_NAME)
    String clientName;

    @JsonProperty(CLIENT_EMAIL)
    String clientEmail;

    @JsonProperty(AMOUNT)
    Double amount;

    @JsonProperty(CREATED)
    LocalDate created;

    @JsonProperty(EVENT_SENT)
    boolean eventSent;

    @JsonCreator
    private static StripePayment apply(
        @JsonProperty(ID) String id,
        @JsonProperty(PAYMENT_TOKEN) String paymentToken,
        @JsonProperty(CHECKOUT_SESSION_ID) String checkoutSessionId,
        @JsonProperty(PAYMENT_INTENT_ID) String paymentIntentId,
        @JsonProperty(CLIENT_NAME) String clientName,
        @JsonProperty(CLIENT_EMAIL) String clientEmail,
        @JsonProperty(AMOUNT) Double amount,
        @JsonProperty(CREATED) LocalDate created,
        @JsonProperty(EVENT_SENT) boolean eventSent
    ) {

        return new StripePayment(
            id, paymentToken, checkoutSessionId, paymentIntentId,
            clientName, clientEmail, amount, created, eventSent
        );
    }

    public static StripePayment createFromCheckoutSession(
        String paymentToken,
        String customerEmail,
        String customerName,
        String checkoutSessionId,
        String paymentIntentId) {

        StripePayment payment = new StripePayment();
        payment.id = UUID.randomUUID().toString();
        payment.paymentToken = paymentToken;
        payment.clientEmail = customerEmail;
        payment.clientName = customerName;
        payment.checkoutSessionId = checkoutSessionId;
        payment.paymentIntentId = paymentIntentId;
        payment.eventSent = false;
        return payment;
    }

    public static StripePayment createFromPaymentIntent(String paymentIntentId, double amount, LocalDate created) {
        StripePayment payment = new StripePayment();
        payment.id = UUID.randomUUID().toString();
        payment.paymentIntentId = paymentIntentId;
        payment.amount = amount;
        payment.created = created;
        payment.eventSent = false;
        return payment;
    }

    /**
     * This method saves the data in the database. It tries to megre the current instance
     * with an instance already present in the databas.
     *
     * @param repository The repository to persist the data in the database.
     */
    public void createOrMergeWithExisting(StripePaymentsRepository repository) {
        var maybeExisting = repository.findOneByPaymentIntentId(paymentIntentId);

        if (maybeExisting.isPresent() && Objects.nonNull(this.checkoutSessionId)) {
            // The current instance has been created from a checkout session event.
            var existing = maybeExisting.get();
            this.id = existing.getId();
            this.amount = existing.amount;
            this.created = existing.created;
        } else if (maybeExisting.isPresent() /* && Objects.isNull(this.checkoutSessionId) */) {
            // The current instance has been created from a payment intent event.
            var existing = maybeExisting.get();
            this.paymentToken = existing.paymentToken;
            this.clientEmail = existing.clientEmail;
            this.clientName = existing.clientName;
            this.checkoutSessionId = existing.checkoutSessionId;
        }

        if (Objects.nonNull(this.paymentToken)
            && Objects.nonNull(this.checkoutSessionId)
            && Objects.nonNull(this.created)
            && !this.eventSent) {

            registerEvent(
                PaymentReceivedEvent.apply(this.paymentToken, this.amount, this.created)
            );

            this.eventSent = true;
        }

        repository.save(this);
    }

    public Optional<Double> getAmount() {
        return Optional.ofNullable(amount);
    }

    public Optional<String> getCheckoutSessionId() {
        return Optional.ofNullable(checkoutSessionId);
    }

    public Optional<String> getClientEmail() {
        return Optional.ofNullable(clientEmail);
    }

    public Optional<String> getClientName() {
        return Optional.ofNullable(clientName);
    }

    public Optional<LocalDate> getCreated() {
        return Optional.ofNullable(created);
    }

    @Override
    public String getId() {
        return id;
    }

    public Optional<String> getPaymentToken() {
        return Optional.ofNullable(paymentToken);
    }

}
