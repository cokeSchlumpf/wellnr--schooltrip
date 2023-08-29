package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.common.markup.Tuple2;
import com.wellnr.schooltrip.core.application.commands.students.RegisterStudentCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolClass;
import com.wellnr.schooltrip.core.model.student.Gender;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandFormBuilder;
import com.wellnr.schooltrip.ui.components.forms.ApplicationFormBuilder;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

import java.time.LocalDate;

@Route(value = "trips/:name/register-student", layout = ApplicationAppLayout.class)
public class SchoolTripAddStudentView extends AbstractSchoolTripView {

    public SchoolTripAddStudentView(
        ApplicationCommandRunner commandRunner, ApplicationUserSession userSession
    ) {
        super(commandRunner, userSession);
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
            RegisterStudentCommand.class,
            commandRunner,
            () -> RegisterStudentCommand.apply(
                schoolTrip.schoolTrip().getName(),
                schoolTrip.schoolTrip().getSchoolClasses().stream().findFirst().map(SchoolClass::getName).orElse(""),
                "",
                "",
                LocalDate.now(),
                Gender.NotSpecified
            )
        )
            .addVariant(
                "schoolTrip", ApplicationFormBuilder.FormVariant.HIDDEN
            )
            .addVariant(
                "schoolClass", ApplicationFormBuilder.FormVariant.LINE_BREAK_AFTER
            )
            .setFieldPossibleValues(
                "schoolClass",
                schoolTrip
                    .schoolTrip()
                    .getSchoolClasses()
                    .stream()
                    .map(sc -> Tuple2.apply(sc.getName(), sc.getName()))
                    .toList()
            )
            .build();

        form.addCompletionListener(event -> UI.getCurrent().navigate(
            SchoolTripView.class, SchoolTripView.getRouteParameters(schoolTrip.schoolTrip().getName())
        ));

        this.add(
            new H3(
                new RouterLink("School Trips", SchoolTripsView.class),
                new Text(" » "),
                new RouterLink(
                    this.schoolTrip.schoolTrip().getTitle(),
                    SchoolTripView.class,
                    SchoolTripView.getRouteParameters(this.schoolTrip.schoolTrip().getName())
                ),
                new Text(" » Register new student")
            ),
            form
        );
    }

}
