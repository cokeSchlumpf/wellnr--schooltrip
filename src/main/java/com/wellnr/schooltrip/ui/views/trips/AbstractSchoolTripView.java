package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.schooltrip.core.application.commands.schooltrip.GetSchoolTripDetailsCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripDetailsProjection;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.infrastructure.UserSession;
import com.wellnr.schooltrip.ui.components.ApplicationRouterLinkWithIcon;
import com.wellnr.schooltrip.ui.layout.AbstractApplicationAppView;

import java.util.List;

public abstract class AbstractSchoolTripView extends AbstractApplicationAppView implements BeforeEnterObserver {

    protected final SchoolTripCommandRunner commandRunner;

    protected SchoolTripDetailsProjection schoolTrip;

    protected AbstractSchoolTripView(
        SchoolTripCommandRunner commandRunner, UserSession userSession
    ) {
        super(userSession);
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

        var payments = new ApplicationRouterLinkWithIcon(
            VaadinIcon.MONEY, "Payments", SchoolTripPaymentsView.class,
            SchoolTripPaymentsView.getRouteParameters(this.schoolTrip.schoolTrip().getName())
        );

        var classes = new ApplicationRouterLinkWithIcon(
            VaadinIcon.LIST, "Classes", SchoolTripClassesView.class,
            SchoolTripClassesView.getRouteParameters(this.schoolTrip.schoolTrip().getName())
        );

        var disciplines = new ApplicationRouterLinkWithIcon(
            VaadinIcon.RECORDS, "Disciplines", SchoolTripDisciplineView.class,
            SchoolTripDisciplineView.getRouteParameters(this.schoolTrip.schoolTrip().getName())
        );

        var tasks = new ApplicationRouterLinkWithIcon(
            VaadinIcon.TASKS, "Tasks", SchoolTripTasksView.class,
            SchoolTripTasksView.getRouteParameters(this.schoolTrip.schoolTrip().getName())
        );

        var settings = new ApplicationRouterLinkWithIcon(
            VaadinIcon.OPTIONS, "Settings", SchoolTripSettingsView.class,
            SchoolTripSettingsView.getRouteParameters(this.schoolTrip.schoolTrip().getName())
        );

        return List.of(
            back,
            overview,
            payments,
            disciplines,
            classes,
            tasks,
            settings
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        super.beforeEnter(beforeEnterEvent);

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
