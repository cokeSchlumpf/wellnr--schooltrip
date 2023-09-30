package com.wellnr.schooltrip.infrastructure;

import com.jayway.jsonpath.JsonPath;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.service.CheckoutService;
import com.wellnr.common.Operators;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.schooltrip.ExportInviteMailingLetterData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContext;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
public class ApplicationRESTAPIController {

    private final ApplicationUserSession userSession;

    private final SchoolTripDomainRegistry domainRegistry;

    @GetMapping("/api/trips/{name}/exports/invitation-mailing")
    public ResponseEntity<InputStreamResource> exportSchoolTripInivitationMailings(
        @PathVariable("name") String schoolTrip) {

        var path = ExportInviteMailingLetterData
            .apply(schoolTrip)
            .run(userSession.getUser(), domainRegistry)
            .getData();

        var contentDisposition = ContentDisposition
            .builder("inline")
            .filename(schoolTrip + "--invitation-mailings.zip")
            .build();

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .headers(headers -> {
                headers.setContentDisposition(contentDisposition);
            })
            .body(new InputStreamResource(
                Operators.suppressExceptions(() -> new FileInputStream(path.toFile()))
            ));
    }

    @PostMapping("/api/payments/processed")
    public ResponseEntity<String> paymentProcessedWebhool(@RequestBody String payload, @RequestHeader("stripe-signature") String sigHeader) {
        var json = JsonPath.parse(payload);
        var type = json.read("$.type", String.class);

        // Read and validate event.
        Event event;
        try {
            event = Webhook.constructEvent(
                payload, sigHeader, domainRegistry.getConfig().getStripe().getWebhookSigningSecret()
            );
        } catch (SignatureVerificationException e) {
            log.warn("Could not validate event signature.", e);
            return ResponseEntity.status(400).body("Could not validate event signature.");
        }

        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            var msg = "Could not deserialize Stripe event.";

            log.warn(msg);
            return ResponseEntity.status(400).body(msg);
        }

        // Handle event
        if (event.getType().equals("payment_intent.succeeded") && stripeObject instanceof PaymentIntent payment) {
            System.out.println(payment.getAmountReceived());
            payment.getAmount();
        } else if (event.getType().equals("checkout.session.completed") && stripeObject instanceof Session session) {
            var checkoutSessionId = session.getId();
            var customerName = session.getCustomerObject().getName();
            var customerEmail = session.getCustomerObject().getEmail();
            var paymentIntentId = session.getPaymentIntentObject().getId();

        } else {
            log.info("Ignore Stripe event: {}", event.getType());
        }

        return ResponseEntity.of(Optional.of("Ok."));
    }

    @GetMapping("/api/payments/checkout/{session}")
    public String paymentInitiated(@PathVariable("session") String session) {
        return session;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return userSession.login(request.getUsername(), request.getPassword());
    }

    @GetMapping("/test")
    public String test() {
        return userSession.getUser().toString();
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    public static class LoginRequest {

        String username;

        String password;

    }

}
