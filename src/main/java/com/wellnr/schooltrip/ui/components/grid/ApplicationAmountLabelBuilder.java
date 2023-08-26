package com.wellnr.schooltrip.ui.components.grid;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.text.DecimalFormat;
import java.util.Objects;

@Value
@With
@AllArgsConstructor(staticName = "apply")
public class ApplicationAmountLabelBuilder {

    /**
     * The amount to be displayed.
     */
    double amount;

    /**
     * The decimal format to turn amount into string.
     */
    DecimalFormat decimalFormat;

    /**
     * The currency to be appended to the amount. Mayy be null.
     */
    String curreny;

    /**
     * If inberted is true, positive numbers will be displayed
     * as red.
     */
    boolean inverted;

    public static ApplicationAmountLabelBuilder apply(double amount) {
        return apply(amount, new DecimalFormat("0.00"), null, false);
    }

    public static ApplicationAmountLabelBuilder apply(double amount, String currency) {
        return apply(amount, new DecimalFormat("0.00"), currency, false);
    }

    public Span build() {
        var span = new Span(decimalFormat.format(amount));

        if (Objects.nonNull(curreny)) {
            span.add(" " + curreny);
        }

        if (inverted) {
            if (amount < 0) {
                span.addClassName(LumoUtility.TextColor.SUCCESS);
            } else if (amount > 0) {
                span.addClassName(LumoUtility.TextColor.ERROR);
            }
        } else {
            if (amount < 0) {
                span.addClassName(LumoUtility.TextColor.ERROR);
            } else if (amount > 0) {
                span.addClassName(LumoUtility.TextColor.SUCCESS);
            }
        }

        return span;
    }


}
