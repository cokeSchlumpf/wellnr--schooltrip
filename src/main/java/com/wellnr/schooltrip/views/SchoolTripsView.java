package com.wellnr.schooltrip.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.views.components.Container;
import com.wellnr.schooltrip.views.layout.SchoolTripAppLayout;
import com.wellnr.schooltrip.views.layout.SchoolTripAppView;

@Route(value = "trips", layout = SchoolTripAppLayout.class)
public class SchoolTripsView extends Container implements SchoolTripAppView {

    private final Grid<SchoolTrip> grid = new Grid<>(SchoolTrip.class);
    private final TextField filterText = new TextField();
    private final SchoolTripDomainRegistry domainRegistry;

    public SchoolTripsView(SchoolTripDomainRegistry domainRegistry) {
        this.domainRegistry = domainRegistry;

        configureGrid();
        updateList();

        this.add(getContent());
    }

    @Override
    public String getSectionTitle() {
        return "School Trips";
    }

    private Component getContent() {
        var layout = new VerticalLayout();
        layout.add(getToolbar());
        layout.add(grid);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        return layout;
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addSchoolTripButton = new Button(
            "Create Trip", VaadinIcon.PLUS_CIRCLE.create()
        );

        addSchoolTripButton.addClickListener((event) -> {
            UI.getCurrent().navigate(CreateSchoolTripView.class);
        });

        var toolbar = new HorizontalLayout(filterText, addSchoolTripButton);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureGrid() {
        grid.setColumns("title", "name");
        grid.addColumn(trip -> trip.getSchoolClasses().size()).setHeader("No. of classes");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        this.grid.setItems(
            this.domainRegistry.getSchoolTrips().findAllSchoolTrips() // TODO: Filter permissions?
        );
    }

}
