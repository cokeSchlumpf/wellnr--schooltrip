package com.wellnr.schooltrip.ui.layout;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.schooltrip.ui.SettingsView;
import com.wellnr.schooltrip.ui.components.ApplicationRouterLinkWithIcon;
import com.wellnr.schooltrip.ui.views.trips.SchoolTripsView;

import java.util.List;

// @JsModule("./copy-to-clipboard.js")
public abstract class AbstractApplicationAppView extends VerticalLayout implements ApplicationAppView {

    @Override
    public List<RouterLink> getMainMenuComponents() {
        return List.of(
            new ApplicationRouterLinkWithIcon(VaadinIcon.LIST, "Trips", SchoolTripsView.class),
            new ApplicationRouterLinkWithIcon(VaadinIcon.OPTIONS, "Settings", SettingsView.class)
        );
    }

}
