package com.wellnr.schooltrip.core.model.student.payments;

import java.util.List;

public class PriceLineItems extends AbstractLineItems<PriceLineItem> {

    private PriceLineItems(List<PriceLineItem> items) {
        super(items, PriceLineItem::amount);
    }

    public static PriceLineItems apply(List<PriceLineItem> items) {
        return new PriceLineItems(items);
    }

}
