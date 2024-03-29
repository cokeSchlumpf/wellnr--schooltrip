package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolClass;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.ApplicationNotifications;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridWithControls;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

import java.util.List;
import java.util.Objects;

@Route(value = "trips/:name/classes", layout = ApplicationAppLayout.class)
public class SchoolTripClassesView extends AbstractSchoolTripView {

    private final SchoolTripMessages i18n;

    public SchoolTripClassesView(
        ApplicationCommandRunner commandRunner, ApplicationUserSession userSession
    ) {
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
        return new RouteParameters(
            new RouteParam("name", name)
        );
    }

    @Override
    protected void updateView() {
        if (Objects.isNull(schoolTrip)) {
            return;
        }

        var grid = new SchoolClassesGrid();
        grid.getGrid().setItems(schoolTrip.schoolTrip().getSchoolClasses());
        this.add(grid);
    }

    private class SchoolClassesGrid extends ApplicationGridWithControls<SchoolClass> {

        public SchoolClassesGrid() {
            var classColumn = this
                .getGrid()
                .addColumn(SchoolClass::getName)
                .setHeader(i18n.schoolClass())
                .setSortable(true)
                .setFrozen(true)
                .setAutoWidth(true);

            this
                .getGrid()
                .addColumn(s -> s.getStudents().size())
                .setHeader(i18n.students())
                .setSortable(true)
                .setFrozen(true)
                .setAutoWidth(true);

            this
                .getGrid()
                .addRemoveColumn((s, event) -> {
                    ApplicationNotifications.error("Not implemented.");
                });

            this.getGrid().setMultiSort(false);

            this.getGrid().sort(List.of(
                new GridSortOrder<>(classColumn, SortDirection.ASCENDING)
            ));

            var bttNew = this.getMenuBar().addItem(i18n.addSchoolClass());
            bttNew.addClickListener(ignore -> UI.getCurrent().navigate(
                SchoolTripAddClassView.class, SchoolTripAddClassView.getRouteParameters(
                    schoolTrip.schoolTrip().getName()
                )
            ));
        }

    }

}
