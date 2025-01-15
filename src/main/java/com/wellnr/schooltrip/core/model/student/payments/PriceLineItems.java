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
            .orElse(0d);
    }

    public double getCashFees() {
        return getItems()
            .stream()
            .filter(PriceLineItem::cash)
            .map(PriceLineItem::amount)
            .reduce(Double::sum)
            .orElse(0d);
    }

    public double getRentalFees() {
        return getItems()
            .stream()
            .filter(PriceLineItem::cash)
            .filter(p -> !p.label().toLowerCase().contains("t-shirt"))
            .map(PriceLineItem::amount)
            .reduce(Double::sum)
            .orElse(0d);
    }

}
