package com.wellnr.schooltrip.core.application.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StripeConfiguration {

    String webhookSigningSecret;

}
