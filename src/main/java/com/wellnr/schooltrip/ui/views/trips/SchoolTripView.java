package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridWithControls;
import com.wellnr.schooltrip.ui.components.student.StudentsGrid;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

@Route(value = "trips/:name", layout = ApplicationAppLayout.class)
public class SchoolTripView extends AbstractSchoolTripGridView {

    public SchoolTripView(SchoolTripCommandRunner commandRunner) {
        super(commandRunner);
    }

    @Override
    protected ApplicationGridWithControls<Student> createStudentsGrid() {
        return new StudentsOverviewGrid();
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

        public StudentsOverviewGrid() {
            this.addDefaultColumnsWithSorting(schoolTrip.schoolTrip());
            this.addRegistrationStatusColumn();
            this.addDisciplineColumns();

            var bttNew = this.getMenuBar().addItem("Add Student");
            bttNew.addClickListener(ignore -> UI.getCurrent().navigate(
                SchoolTripAddStudentView.class, SchoolTripAddStudentView.getRouteParameters(
                    schoolTrip.schoolTrip().getName()
                )
            ));
        }

    }

}
