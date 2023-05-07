package com.wellnr.schooltrip.views.components;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class Container extends Div {

    public Container() {
        this.setMaxWidth(960, Unit.PIXELS);
        this.addClassNames(LumoUtility.Margin.AUTO);
        this.setSizeFull();
    }

}
