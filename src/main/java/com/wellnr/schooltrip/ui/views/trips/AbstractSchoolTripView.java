package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.schooltrip.core.application.commands.GetSchoolTripDetailsCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripDetailsProjection;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.ui.components.ApplicationRouterLinkWithIcon;
import com.wellnr.schooltrip.ui.layout.ApplicationAppView;

import java.util.List;

public abstract class AbstractSchoolTripView extends VerticalLayout implements ApplicationAppView, BeforeEnterObserver {

    protected final SchoolTripCommandRunner commandRunner;

    protected SchoolTripDetailsProjection schoolTrip;

    protected AbstractSchoolTripView(SchoolTripCommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    @Override
    public List<RouterLink> getMainMenuComponents() {
        var back = new ApplicationRouterLinkWithIcon(
            VaadinIcon.ARROW_LEFT, "All Workspaces", SchoolTripsView.class
        );
        back.setHighlightCondition(HighlightConditions.never());
        back.setDivider(true);

        var overview = new ApplicationRouterLinkWithIcon(
            VaadinIcon.DASHBOARD, "Overview", SchoolTripView.class,
            SchoolTripView.getRouteParameters(this.schoolTrip.schoolTrip().getName())
        );
        overview.setHighlightCondition(HighlightConditions.sameLocation());

        var tasks = new ApplicationRouterLinkWithIcon(
            VaadinIcon.TASKS, "Tasks", SchoolTripTasksView.class,
            SchoolTripTasksView.getRouteParameters(this.schoolTrip.schoolTrip().getName())
        );

        return List.of(
            back,
            overview,
            tasks
        );
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
