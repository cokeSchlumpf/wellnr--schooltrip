package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.schooltrip.core.application.commands.CreateSchoolTripCommand;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.infrastructure.UserSession;
import com.wellnr.schooltrip.ui.components.ApplicationContentContainer;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandFormBuilder;
import com.wellnr.schooltrip.ui.layout.AbstractApplicationAppView;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;
import com.wellnr.schooltrip.ui.layout.ApplicationAppView;
import com.wellnr.schooltrip.ui.views.trips.SchoolTripView;
import com.wellnr.schooltrip.ui.views.trips.SchoolTripsView;

@Route(value = "trips/create", layout = ApplicationAppLayout.class)
public class CreateSchoolTripView extends AbstractApplicationAppView implements ApplicationAppView {

    public CreateSchoolTripView(SchoolTripCommandRunner commandRunner, UserSession userSession) {
        var form = new ApplicationCommandFormBuilder<>(
            CreateSchoolTripCommand.class,
            commandRunner,
            () -> CreateSchoolTripCommand.apply("", "")
        ).addVariant(
            "title",
            ApplicationCommandFormBuilder.FormVariant.FULL_WIDTH
        ).addVariant(
            "name",
            ApplicationCommandFormBuilder.FormVariant.FULL_WIDTH
        ).build();

        form.addCompletionListener(event -> UI.getCurrent().navigate(
            SchoolTripView.class,
            SchoolTripView.getRouteParameters(event.getResult().getData().getName())
        ));

        this.add(
            new H3(
                new RouterLink("School Trips", SchoolTripsView.class),
                new Text(" » Create new school trip")
            ),

            new ApplicationContentContainer(
                new Paragraph("Create new school trip."),
                form
            )
        );
    }

}
