package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.wellnr.common.markup.Nothing;
import com.wellnr.common.markup.Tuple2;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.application.commands.AddPaymentCommand;
import com.wellnr.schooltrip.core.application.commands.ConfirmStudentRegistrationCommand;
import com.wellnr.schooltrip.core.application.commands.RegisterStudentCommand;
import com.wellnr.schooltrip.core.application.commands.UpdateStudentPropertiesCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolClass;
import com.wellnr.schooltrip.core.model.student.RegistrationState;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.questionaire.Ski;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.ui.components.ApplicationNotifications;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandForm;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandFormBuilder;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridWithControls;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

import java.util.List;
import java.util.Objects;

@Route(value = "trips/:name/classes", layout = ApplicationAppLayout.class)
public class SchoolTripClassesView extends AbstractSchoolTripView {

    public SchoolTripClassesView(SchoolTripCommandRunner commandRunner) {
        super(commandRunner);
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
                .setHeader("Class")
                .setSortable(true)
                .setFrozen(true)
                .setAutoWidth(true);

            var studentsCount = this
                .getGrid()
                .addColumn(s -> s.getStudents().size())
                .setHeader("Students")
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

            var bttNew = this.getMenuBar().addItem("Add Class");
            bttNew.addClickListener(ignore -> UI.getCurrent().navigate(
                SchoolTripCreateClassView.class, SchoolTripCreateClassView.getRouteParameters(
                    schoolTrip.schoolTrip().getName()
                )
            ));
        }

    }

}
