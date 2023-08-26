package com.wellnr.schooltrip.core.model.student.payments;

import java.util.List;

public class Payments extends AbstractLineItems<Payment> {

    private Payments(List<Payment> items) {
        super(items, Payment::getAmount);
    }

    public static Payments apply(List<Payment> items) {
        return new Payments(items);
    }

}
