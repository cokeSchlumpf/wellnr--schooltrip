package com.wellnr.schooltrip.views.components;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class Container extends Div {

    private static final int MAX_WIDTH_NON_FLUID = 960;

    private boolean fluid;

    public Container() {
        this(false);
        this.setSizeFull();
    }

    public Container(boolean fluid) {
        this.addClassNames(LumoUtility.Margin.AUTO);
        this.setSizeFull();

        this.setFluid(fluid);
    }

    public void setFluid(boolean fluid) {
        if (!fluid) {
            this.setMaxWidth(MAX_WIDTH_NON_FLUID, Unit.PIXELS);
        } else {
            this.setMaxWidth(100, Unit.PERCENTAGE);
        }

        this.fluid = fluid;
    }

    public boolean isFluid() {
        return fluid;
    }

}
