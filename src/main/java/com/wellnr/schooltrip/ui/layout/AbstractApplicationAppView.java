package com.wellnr.schooltrip.ui.layout;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.schooltrip.core.model.user.rbac.DomainPermission;
import com.wellnr.schooltrip.infrastructure.UserSession;
import com.wellnr.schooltrip.ui.LoginView;
import com.wellnr.schooltrip.ui.components.ApplicationRouterLinkWithIcon;
import com.wellnr.schooltrip.ui.views.admin.SettingsView;
import com.wellnr.schooltrip.ui.views.trips.SchoolTripsView;

import java.util.List;

// @JsModule("./copy-to-clipboard.js")
public abstract class AbstractApplicationAppView extends VerticalLayout implements ApplicationAppView, BeforeEnterObserver {

    private final UserSession userSession;

    private final List<DomainPermission> minimumPermissions;

    public AbstractApplicationAppView(UserSession userSession, List<DomainPermission> minimumPermissions) {
        this.userSession = userSession;
        this.minimumPermissions = minimumPermissions;
    }

    public AbstractApplicationAppView(UserSession userSession) {
        this(userSession, List.of());
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
        var maybeUser = userSession.getRegisteredUser();

        if (maybeUser.isEmpty()) {
            event.forwardTo(LoginView.class);
        } else if (!minimumPermissions.isEmpty()) {
            var user = maybeUser.get();
            user.checkPermission(minimumPermissions);
        }
    }

}

