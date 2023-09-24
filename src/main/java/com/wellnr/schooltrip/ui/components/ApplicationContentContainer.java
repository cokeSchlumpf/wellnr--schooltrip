package com.wellnr.schooltrip.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ApplicationContentContainer extends VerticalLayout {

    public ApplicationContentContainer() {
        this.setPadding(false);
        this.setMaxWidth(800, Unit.PIXELS);
    }

    public ApplicationContentContainer(Component... components) {
        this();
        this.add(components);
    }

}
