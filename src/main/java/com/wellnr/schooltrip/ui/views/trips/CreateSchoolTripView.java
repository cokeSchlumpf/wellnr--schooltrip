package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.schooltrip.core.application.commands.schooltrip.CreateSchoolTripCommand;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.ApplicationContentContainer;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandFormBuilder;
import com.wellnr.schooltrip.ui.layout.AbstractApplicationAppView;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;
import com.wellnr.schooltrip.ui.layout.ApplicationAppView;

@Route(value = "trips/create", layout = ApplicationAppLayout.class)
public class CreateSchoolTripView extends AbstractApplicationAppView implements ApplicationAppView {

    public CreateSchoolTripView(ApplicationCommandRunner commandRunner, ApplicationUserSession userSession) {
        super(userSession);

        var i18n = userSession.getMessages();

        var form = new ApplicationCommandFormBuilder<>(
            CreateSchoolTripCommand.class,
            commandRunner,
            () -> CreateSchoolTripCommand.apply("", "")
        )
            .addVariant(
                "title",
                ApplicationCommandFormBuilder.FormVariant.FULL_WIDTH
            )
            .addVariant(
                "name",
                ApplicationCommandFormBuilder.FormVariant.FULL_WIDTH
            )
            .setI18nMessages(i18n)
            .build();

        form.addCompletionListener(event -> UI.getCurrent().navigate(
            SchoolTripView.class,
            SchoolTripView.getRouteParameters(event.getResult().getData().getName())
        ));

        this.add(
            new H3(
                new RouterLink(i18n.schoolTrips(), SchoolTripsView.class),
                new Text(" » " + i18n.addSchoolTrip())
            ),

            new ApplicationContentContainer(
                form
            )
        );
    }

}
