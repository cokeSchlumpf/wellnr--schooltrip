package com.wellnr.schooltrip.ui.layout;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.schooltrip.core.application.commands.users.GetUserApplicationPermissionsCommand;
import com.wellnr.schooltrip.core.model.user.rbac.DomainPermission;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.LoginView;
import com.wellnr.schooltrip.ui.components.ApplicationRouterLinkWithIcon;
import com.wellnr.schooltrip.ui.views.admin.SettingsView;
import com.wellnr.schooltrip.ui.views.trips.SchoolTripsView;

import java.util.ArrayList;
import java.util.List;

// @JsModule("./copy-to-clipboard.js")
public abstract class AbstractApplicationAppView extends VerticalLayout implements ApplicationAppView, BeforeEnterObserver {

    protected final ApplicationUserSession userSession;

    protected final List<DomainPermission> minimumPermissions;

    public AbstractApplicationAppView(
        ApplicationUserSession userSession, List<DomainPermission> minimumPermissions
    ) {

        this.userSession = userSession;
        this.minimumPermissions = minimumPermissions;
    }

    public AbstractApplicationAppView(
        ApplicationUserSession userSession
    ) {
        this(userSession, List.of());
    }

    @Override
    public List<RouterLink> getMainMenuComponents() {
        var menuItems = new ArrayList<RouterLink>();
        menuItems.add(
            new ApplicationRouterLinkWithIcon(VaadinIcon.LIST, "Trips", SchoolTripsView.class)
        );

        if (userSession.getPermissions().isCanManageApplication()) {
            menuItems.add(
                new ApplicationRouterLinkWithIcon(VaadinIcon.OPTIONS, "Settings", SettingsView.class)
            );
        }

        return menuItems;
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

