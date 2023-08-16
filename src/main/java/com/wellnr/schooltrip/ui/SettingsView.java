package com.wellnr.schooltrip.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;
import com.wellnr.schooltrip.ui.layout.ApplicationAppView;

import java.util.List;

@Route(value = "settings", layout = ApplicationAppLayout.class)
public class SettingsView extends Div implements ApplicationAppView {

    public SettingsView() {
        this.add(new Span("Settings View"));
    }

    @Override
    public List<RouterLink> getMainMenuComponents() {
        return List.of();
    }

}
