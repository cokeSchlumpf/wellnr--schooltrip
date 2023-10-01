package com.wellnr.schooltrip.core.model.student.payments;

import java.util.List;

public class PriceLineItems extends AbstractLineItems<PriceLineItem> {

    private PriceLineItems(List<PriceLineItem> items) {
        super(items, PriceLineItem::amount);
    }

    public static PriceLineItems apply(List<PriceLineItem> items) {
        return new PriceLineItems(items);
    }

    public double getAmountPaymentsBeforeTrip() {
        return getItems()
            .stream()
            .filter(p -> !p.cash())
            .map(PriceLineItem::amount)
            .reduce(Double::sum)
            .map(d -> Double.valueOf(format.format(d)))
            .orElse(0d);
    }

    public double getRentalFees() {
        return getItems()
            .stream()
            .filter(PriceLineItem::cash)
            .map(PriceLineItem::amount)
            .reduce(Double::sum)
            .map(d -> Double.valueOf(format.format(d)))
            .orElse(0d);
    }

}
