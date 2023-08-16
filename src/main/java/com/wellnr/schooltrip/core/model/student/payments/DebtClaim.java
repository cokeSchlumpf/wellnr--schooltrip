package com.wellnr.schooltrip.core.model.student.payments;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DebtClaim implements Transaction {

    String description;

    double amount;

}
