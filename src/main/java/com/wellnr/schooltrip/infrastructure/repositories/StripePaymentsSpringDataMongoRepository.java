package com.wellnr.schooltrip.infrastructure.repositories;

import com.wellnr.schooltrip.core.model.stripe.StripePayment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StripePaymentsSpringDataMongoRepository extends MongoRepository<StripePayment, String> {

    Optional<StripePayment> findOneByPaymentIntentId(String paymentIntentId);

    Optional<StripePayment> findByCheckoutSessionId(String checkoutSessionId);

}
