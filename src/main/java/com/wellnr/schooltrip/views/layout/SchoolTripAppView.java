package com.wellnr.schooltrip.views.layout;

import com.vaadin.flow.component.Component;

import java.util.Optional;

public interface SchoolTripAppView {

    String getSectionTitle();

    default Optional<Component> getSubmenu() {
        return Optional.empty();
    }

}
