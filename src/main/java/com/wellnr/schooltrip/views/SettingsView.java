package com.wellnr.schooltrip.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import com.wellnr.schooltrip.views.layout.SchoolTripAppLayout;
import com.wellnr.schooltrip.views.layout.SchoolTripAppView;

@Route(value = "settings", layout = SchoolTripAppLayout.class)
public class SettingsView extends Div implements SchoolTripAppView {

    public SettingsView() {
        this.add(new Span("Settings View"));
    }

    @Override
    public String getSectionTitle() {
        return "Settings";
    }
}
