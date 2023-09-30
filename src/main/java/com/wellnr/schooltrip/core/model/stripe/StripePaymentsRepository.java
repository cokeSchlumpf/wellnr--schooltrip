package com.wellnr.schooltrip.core.model.stripe;

import java.util.Optional;

public interface StripePaymentsRepository {

    void save(StripePayment stripePayment);

    Optional<StripePayment> findeOneByCheckoutSessionId(String checkoutSessionId);

    Optional<StripePayment> findOneByPaymentIntentId(String paymentIntentId);

}
