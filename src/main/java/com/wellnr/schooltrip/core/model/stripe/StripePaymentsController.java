package com.wellnr.schooltrip.core.model.stripe;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;

@Slf4j
@RestController
@AllArgsConstructor
public class StripePaymentsController {

    private final SchoolTripDomainRegistry domainRegistry;

    @PostMapping("/api/payments/events")
    public void paymentProcessedWebhook(
        @RequestBody String payload,
        @RequestHeader("stripe-signature") String sigHeader,
        HttpServletResponse response) {

        // Read and validate event.
        Event event;
        try {
            event = Webhook.constructEvent(
                payload, sigHeader, domainRegistry.getConfig().getStripe().getWebhookSigningSecret()
            );
        } catch (SignatureVerificationException e) {
            log.warn("Could not validate event signature.", e);

            response.setStatus(400);
            try (var writer = response.getWriter()) {
                writer.write("Could not validate event signature.");
                writer.flush();
            } catch (Exception e1) {
                log.error("Could not write response.", e1);
            }

            return;
        }

        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            response.setStatus(400);
            try (var writer = response.getWriter()) {
                writer.write("Could not deserialize Stripe event.");
                writer.flush();
            } catch (Exception e1) {
                log.error("Could not write response.", e1);
            }

            return;
        }

        // Send successful response
        try {
            response.setStatus(200);
            try (var writer = response.getWriter()) {
                writer.write("Ok.");
                writer.flush();
            } catch (Exception e1) {
                log.error("Could not write response.", e1);
            }

            // Handle event
            if (event.getType().equals("payment_intent.succeeded") && stripeObject instanceof PaymentIntent payment) {
                StripePayment
                    .createFromPaymentIntent(
                        payment.getId(),
                        (double) payment.getAmount() / 100,
                        Instant.ofEpochSecond(payment.getCreated()).atZone(ZoneId.systemDefault()).toLocalDate()
                    )
                    .createOrMergeWithExisting(domainRegistry.getPayments());
            } else if (
                event.getType().equals("checkout.session.completed") && stripeObject instanceof Session session
            ) {

                var checkoutSessionId = session.getId();
                var customerName = session.getCustomer();
                var customerEmail = session.getCustomerEmail();
                var paymentIntentId = session.getPaymentIntent();
                var paymentToken = session.getClientReferenceId();

                StripePayment
                    .createFromCheckoutSession(
                        paymentToken, customerEmail, customerName, checkoutSessionId, paymentIntentId
                    )
                    .createOrMergeWithExisting(domainRegistry.getPayments());
            } else {
                log.debug("Ignore Stripe event: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("An exception has occurred while processing stripe payment event.", e);
        }
    }

}
