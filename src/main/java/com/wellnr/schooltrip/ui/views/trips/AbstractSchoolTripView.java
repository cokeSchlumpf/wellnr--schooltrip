package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.*;
import com.wellnr.schooltrip.core.application.commands.schooltrip.GetSchoolTripDetailsCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripDetailsProjection;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.ApplicationRouterLinkWithIcon;
import com.wellnr.schooltrip.ui.layout.AbstractApplicationAppView;

import java.util.List;
import java.util.Objects;

public abstract class AbstractSchoolTripView extends AbstractApplicationAppView implements BeforeEnterObserver, HasDynamicTitle {

    protected final ApplicationCommandRunner commandRunner;
    private final SchoolTripMessages i18n;
    protected SchoolTripDetailsProjection schoolTrip;

    protected AbstractSchoolTripView(
        ApplicationCommandRunner commandRunner, ApplicationUserSession userSession
    ) {
        super(userSession);
        this.commandRunner = commandRunner;
        this.i18n = userSession.getMessages();
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

    @Override
    public String getPageTitle() {
        if (Objects.nonNull(schoolTrip)) {
            return super.getPageTitle() + " - " +  schoolTrip.schoolTrip().getTitle();
        } else {
            return super.getPageTitle();
        }
    }

    @Override
    public List<RouterLink> getMainMenuComponents() {
        var back = new ApplicationRouterLinkWithIcon(
            VaadinIcon.ARROW_LEFT, i18n.allSchoolTrips(), SchoolTripsView.class
        );
        back.setHighlightCondition(HighlightConditions.never());
        back.setDivider(true);

        var overview = new ApplicationRouterLinkWithIcon(
            VaadinIcon.DASHBOARD, i18n.overview(), SchoolTripView.class,
            SchoolTripView.getRouteParameters(this.schoolTrip.schoolTrip().getName())
        );
        overview.setHighlightCondition(HighlightConditions.sameLocation());

        var payments = new ApplicationRouterLinkWithIcon(
            VaadinIcon.MONEY, i18n.payments(), SchoolTripPaymentsView.class,
            SchoolTripPaymentsView.getRouteParameters(this.schoolTrip.schoolTrip().getName())
        );

        var classes = new ApplicationRouterLinkWithIcon(
            VaadinIcon.LIST, i18n.schoolClasses(), SchoolTripClassesView.class,
            SchoolTripClassesView.getRouteParameters(this.schoolTrip.schoolTrip().getName())
        );

        var disciplines = new ApplicationRouterLinkWithIcon(
            VaadinIcon.RECORDS, i18n.disciplines(), SchoolTripDisciplineView.class,
            SchoolTripDisciplineView.getRouteParameters(this.schoolTrip.schoolTrip().getName())
        );

        var tasks = new ApplicationRouterLinkWithIcon(
            VaadinIcon.TASKS, i18n.tasks(), SchoolTripTasksView.class,
            SchoolTripTasksView.getRouteParameters(this.schoolTrip.schoolTrip().getName())
        );

        var settings = new ApplicationRouterLinkWithIcon(
            VaadinIcon.OPTIONS, i18n.settings(), SchoolTripSettingsView.class,
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

    protected void reload() {
        this.schoolTrip = commandRunner
            .run(GetSchoolTripDetailsCommand.apply(this.schoolTrip.schoolTrip().getName()))
            .getData();

        this.updateView();
    }

    protected abstract void updateView();

}
