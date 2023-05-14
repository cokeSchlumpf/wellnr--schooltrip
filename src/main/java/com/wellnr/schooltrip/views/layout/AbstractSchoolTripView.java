package com.wellnr.schooltrip.views.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.schooltrip.core.application.commands.GetSchoolTripDetailsCommand;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.views.*;
import com.wellnr.schooltrip.views.components.Container;
import com.wellnr.schooltrip.views.components.RouterTabs;

import java.util.Objects;
import java.util.Optional;

public abstract class AbstractSchoolTripView extends Container implements SchoolTripAppView, BeforeEnterObserver {

    protected final SchoolTripCommandRunner commandRunner;

    protected GetSchoolTripDetailsCommand.SchoolTripDetailsProjection schoolTrip;

    protected AbstractSchoolTripView(boolean fluid, SchoolTripCommandRunner commandRunner) {
        super(fluid);
        this.commandRunner = commandRunner;
    }

    @Override
    public Optional<Component> getSubmenu() {
        var tabs = new RouterTabs();
        tabs.add(new RouterLink("Overview", SchoolTripView.class, new RouteParameters("name", this.schoolTrip.schoolTrip().getName())));
        tabs.add(new RouterLink("Settings", SchoolTripSettingsView.class, new RouteParameters("name", this.schoolTrip.schoolTrip().getName())));

        return Optional.of(tabs);
    }

    @Override
    public String getSectionTitle() {
        if (Objects.isNull(schoolTrip)) {
            return "School Trip";
        } else {
            return this.schoolTrip.schoolTrip().getTitle();
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        var name = beforeEnterEvent
            .getRouteParameters()
            .get("name")
            .orElseThrow();

        this.schoolTrip = commandRunner
            .run(GetSchoolTripDetailsCommand.apply(name))
            .getData();

        this.updateView();
    }

    protected void reload() {
        this.schoolTrip = commandRunner
            .run(GetSchoolTripDetailsCommand.apply(this.schoolTrip.schoolTrip().getName()))
            .getData();

        this.updateView();
    }

    protected abstract void updateView();

}
