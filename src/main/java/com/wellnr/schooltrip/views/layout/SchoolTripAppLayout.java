package com.wellnr.schooltrip.views.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.wellnr.schooltrip.infrastructure.UserSession;
import com.wellnr.schooltrip.views.ProfileView;
import com.wellnr.schooltrip.views.SchoolTripsView;
import com.wellnr.schooltrip.views.SettingsView;
import com.wellnr.schooltrip.views.components.RouterTabs;

public class SchoolTripAppLayout extends AppLayout {

    private final H2 areaTitle;

    private final VerticalLayout subMenuContainer;

    private final UserSession userSession;

    public SchoolTripAppLayout(UserSession userSession) {
        setPrimarySection(Section.DRAWER);

        this.userSession = userSession;
        this.areaTitle = new H2("");
        this.areaTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        this.subMenuContainer = new VerticalLayout();
        this.subMenuContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        this.subMenuContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        this.subMenuContainer.setSizeUndefined();

        addNavBarContent();
        addDrawerContent();
    }

    private void addDrawerContent() {
        /*
         * App title
         */
        var appName = new H1("School Trips");
        appName.addClassNames(LumoUtility.FontSize.LARGE);

        var header = new Header(appName);
        header.addClassNames(LumoUtility.Margin.LARGE);

        /*
         * Tabs
         */
        var tabs = new RouterTabs();
        tabs.add(new RouterLink("School Trips", SchoolTripsView.class), link -> new Tab(VaadinIcon.TASKS.create(), link));
        tabs.add(new RouterLink("Settings", SettingsView.class), link -> new Tab(VaadinIcon.COG.create(), link));
        tabs.add(new RouterLink("Profile", ProfileView.class), link -> new Tab(VaadinIcon.USER.create(), link));
        tabs.setOrientation(Tabs.Orientation.VERTICAL);

        var scroller = new Scroller(tabs);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);

        /*
         * Footer
         */
        var footer = new Footer(new Text("(c) wellnr.com"));

        /*
         * Finalize
         */
        addToDrawer(header, scroller, footer);
        this.setDrawerOpened(false);
    }

    private void addNavBarContent() {
        /*
         * Drawer
         */
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        /*
         * User Menu
         */
        var logout = new Button("Logout");
        logout.addThemeVariants(ButtonVariant.LUMO_SMALL);

        var userMenu = new HorizontalLayout();
        userMenu.setAlignItems(FlexComponent.Alignment.CENTER);
        userMenu.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        userMenu.addClassNames(LumoUtility.Margin.Right.LARGE);
        userMenu.add(new Text("Michael"), logout); // TODO, get from Session.

        var layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        layout.add(this.areaTitle, this.subMenuContainer, userMenu);

        addToNavbar(true, toggle, layout);
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        super.showRouterLayoutContent(content);

        if (content instanceof SchoolTripAppView view) {
            this.areaTitle.setText(view.getSectionTitle());

            this.subMenuContainer.removeAll();
            var maybeSubmenu = view.getSubmenu();
            maybeSubmenu.ifPresent(this.subMenuContainer::add);
        }
    }

}
