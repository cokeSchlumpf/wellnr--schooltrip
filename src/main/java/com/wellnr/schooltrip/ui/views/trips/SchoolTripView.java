package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridWithControls;
import com.wellnr.schooltrip.ui.components.student.StudentsGrid;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

@Route(value = "trips/:name", layout = ApplicationAppLayout.class)
public class SchoolTripView extends AbstractSchoolTripGridView {

    private final SchoolTripMessages i18n;

    public SchoolTripView(
        ApplicationCommandRunner commandRunner, ApplicationUserSession userSession
    ) {
        super(commandRunner, userSession);
        i18n = userSession.getMessages();
    }

    @Override
    protected ApplicationGridWithControls<Student> createStudentsGrid() {
        return new StudentsOverviewGrid(i18n);
    }

    /**
     * Helper method to generate the route parameters for this view.
     *
     * @param name The name of the school trip to route to.
     * @return The set of route parameters required to call this view.
     */
    public static RouteParameters getRouteParameters(String name) {
        return new RouteParameters(
            new RouteParam("name", name)
        );
    }

    private class StudentsOverviewGrid extends StudentsGrid {

        public StudentsOverviewGrid(SchoolTripMessages i18n) {
            super(i18n);
            this.addDefaultColumnsWithSorting(schoolTrip.schoolTrip());
            this.addRegistrationStatusColumn();
            this.addDisciplineColumns();

            var bttNew = this.getMenuBar().addItem(i18n.addStudent());
            bttNew.addClickListener(ignore -> UI.getCurrent().navigate(
                SchoolTripAddStudentView.class, SchoolTripAddStudentView.getRouteParameters(
                    schoolTrip.schoolTrip().getName()
                )
            ));
        }

    }

}
