package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.schooltrip.core.application.commands.schooltrip.ListSchoolTripsCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridWithControls;
import com.wellnr.schooltrip.ui.layout.AbstractApplicationAppView;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

import java.util.List;

@Route(value = "trips", layout = ApplicationAppLayout.class)
public class SchoolTripsView extends AbstractApplicationAppView {

    private final SchoolTripsGrid schoolTripsGrid;
    private final ApplicationCommandRunner commandRunner;

    public SchoolTripsView(
        ApplicationCommandRunner commandRunner, ApplicationUserSession userSession
    ) {
        super(userSession);

        this.commandRunner = commandRunner;
        this.schoolTripsGrid = new SchoolTripsGrid();

        this.add(
            schoolTripsGrid
        );

        refreshTrips();
    }

    private void refreshTrips() {
        var result = commandRunner.run(ListSchoolTripsCommand.apply());
        this.schoolTripsGrid.getGrid().setItems(result.getData());
    }

    private class SchoolTripsGrid extends ApplicationGridWithControls<SchoolTrip> {

        public SchoolTripsGrid() {
            var schoolTripColumn = this
                .getGrid()
                .addComponentColumn(trip -> new RouterLink(
                    trip.getTitle(), SchoolTripView.class, SchoolTripView.getRouteParameters(trip.getName())
                ))
                .setHeader(userSession.getMessages().schoolTrip())
                .setSortable(true);

            this.getGrid().sort(List.of(
                new GridSortOrder<>(schoolTripColumn, SortDirection.ASCENDING)
            ));

            if (userSession.getPermissions().isCanManageSchoolTrips()) {
                var bttNew = this.getMenuBar().addItem(
                    userSession.getMessages().createSchoolTrip()
                );

                bttNew.addClickListener(ignore -> UI.getCurrent().navigate(CreateSchoolTripView.class));
            }

        }

    }

}
