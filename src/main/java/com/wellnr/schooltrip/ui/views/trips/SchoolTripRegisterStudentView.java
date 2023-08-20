package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.common.functions.Procedure0;
import com.wellnr.schooltrip.core.application.commands.RegisterStudentCommand;
import com.wellnr.schooltrip.core.application.commands.RegisterStudentsCommand;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.ui.components.ApplicationCard;
import com.wellnr.schooltrip.ui.components.ExcelImportDialog;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

import java.util.List;

@Route(value = "trips/:name/register-student", layout = ApplicationAppLayout.class)
public class SchoolTripRegisterStudentView extends AbstractSchoolTripView {

    public SchoolTripRegisterStudentView(SchoolTripCommandRunner commandRunner) {
        super(commandRunner);
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
        this.add(
            new H3(
                new RouterLink("School Trips", SchoolTripsView.class),
                new RouterLink(
                    this.schoolTrip.schoolTrip().getTitle(),
                    SchoolTripView.class,
                    SchoolTripView.getRouteParameters(this.schoolTrip.schoolTrip().getName())
                ),
                new Text(" » Register new student")
            )
        );
    }

}
