package com.wellnr.schooltrip.ui.layout;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.schooltrip.infrastructure.UserSession;
import com.wellnr.schooltrip.ui.LoginView;
import com.wellnr.schooltrip.ui.views.admin.SettingsView;
import com.wellnr.schooltrip.ui.components.ApplicationRouterLinkWithIcon;
import com.wellnr.schooltrip.ui.views.trips.SchoolTripsView;

import java.util.List;

// @JsModule("./copy-to-clipboard.js")
public abstract class AbstractApplicationAppView extends VerticalLayout implements ApplicationAppView, BeforeEnterObserver {

    private final UserSession userSession;

    public AbstractApplicationAppView(UserSession userSession) {
        this.userSession = userSession;
    }

    @Override
    public List<RouterLink> getMainMenuComponents() {
        return List.of(
            new ApplicationRouterLinkWithIcon(VaadinIcon.LIST, "Trips", SchoolTripsView.class),
            new ApplicationRouterLinkWithIcon(VaadinIcon.OPTIONS, "Settings", SettingsView.class)
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Don't allow access to pages if user is not
        if (userSession.getRegisteredUser().isEmpty()) {
            event.forwardTo(LoginView.class);
        }
    }

}

