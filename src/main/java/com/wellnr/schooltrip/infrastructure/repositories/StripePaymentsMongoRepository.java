package com.wellnr.schooltrip.infrastructure.repositories;

import com.wellnr.schooltrip.core.model.stripe.StripePayment;
import com.wellnr.schooltrip.core.model.stripe.StripePaymentsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class StripePaymentsMongoRepository implements StripePaymentsRepository {

    private final StripePaymentsSpringDataMongoRepository spring;

    @Override
    public void save(StripePayment stripePayment) {
        spring.save(stripePayment);
    }

    @Override
    public Optional<StripePayment> findeOneByCheckoutSessionId(String checkoutSessionId) {
        return spring.findByCheckoutSessionId(checkoutSessionId);
    }

    @Override
    public Optional<StripePayment> findOneByPaymentIntentId(String paymentIntentId) {
        return spring.findOneByPaymentIntentId(paymentIntentId);
    }

}
