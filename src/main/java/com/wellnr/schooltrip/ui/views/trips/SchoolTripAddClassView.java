package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.schooltrip.core.application.commands.schooltrip.RegisterSchoolClassCommand;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandFormBuilder;
import com.wellnr.schooltrip.ui.components.forms.ApplicationFormBuilder;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

@Route(value = "trips/:name/create-class", layout = ApplicationAppLayout.class)
public class SchoolTripAddClassView extends AbstractSchoolTripView {

    private final SchoolTripMessages i18n;

    public SchoolTripAddClassView(ApplicationCommandRunner commandRunner, ApplicationUserSession userSession) {
        super(commandRunner, userSession);
        this.i18n = userSession.getMessages();
    }

    /**
     * Helper method to generate the route parameters for this view.
     *
     * @param name The name of the school trip to route to.
     * @return The set of route parameters required to call this view.
     */
    public static RouteParameters getRouteParameters(String name) {
        return SchoolTripView.getRouteParameters(name);
    }

    @Override
    protected void updateView() {
        this.removeAll();

        var form = new ApplicationCommandFormBuilder<>(
            RegisterSchoolClassCommand.class, commandRunner,
            () -> RegisterSchoolClassCommand.apply(schoolTrip.schoolTrip().getName(), "")
        )
            .addVariant("schoolTrip", ApplicationFormBuilder.FormVariant.HIDDEN)
            .addVariant("name", ApplicationFormBuilder.FormVariant.FULL_WIDTH)
            .setI18nMessages(i18n)
            .build();

        form.addCompletionListener(e -> UI.getCurrent().navigate(
            SchoolTripClassesView.class,
            SchoolTripClassesView.getRouteParameters(schoolTrip.schoolTrip().getName())
        ));

        this.add(
            new H3(
                new RouterLink(i18n.schoolTrips(), SchoolTripsView.class),
                new Text(" » "),
                new RouterLink(
                    this.schoolTrip.schoolTrip().getTitle(),
                    SchoolTripView.class,
                    SchoolTripView.getRouteParameters(this.schoolTrip.schoolTrip().getName())
                ),
                new Text(" » " + i18n.addSchoolClass())
            ),
            form
        );
    }

}
