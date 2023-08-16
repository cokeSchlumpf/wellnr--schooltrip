package com.wellnr.schooltrip.core.model.student.payments;

public sealed interface Transaction permits Payment, DebtClaim {
}
