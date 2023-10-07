package com.wellnr.schooltrip.core.model.student.payments;

import com.wellnr.common.functions.Function1;

import java.text.DecimalFormat;
import java.util.List;

public abstract class AbstractLineItems<T> {

    private final List<T> items;

    private final Function1<T, Double> getAmount;

    protected AbstractLineItems(List<T> items, Function1<T, Double> getAmount) {
        this.items = List.copyOf(items);
        this.getAmount = getAmount;
    }

    public List<T> getItems() {
        return items;
    }

    public double getSum() {
        return items
            .stream()
            .map(getAmount::get)
            .reduce(Double::sum)
            .orElse(0d);
    }

    public String getSumFormatted(DecimalFormat format) {
        return format.format(getSum()) + " €";
    }

}
