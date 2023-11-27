package com.wellnr.schooltrip.ui.components.grid;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializablePredicate;
import com.wellnr.common.functions.Function2;
import lombok.Getter;

/**
 * A composed Grid component which offers a menu bar and a query field to filter items in the grid.
 *
 * @param <T> The type of the grid's items.
 */
public class ApplicationGridWithControls<T> extends VerticalLayout {

    /**
     *  Access the underlying grid component.
     */
    @Getter
    private final ApplicationGrid<T> grid;

    @Getter
    private final MenuBar menuBar;

    /**
     *  Gets the filter query input field of the control.
     */
    @Getter
    private final TextField filterQuery;

    private Function2<String, T, Boolean> filterFunction;

    public ApplicationGridWithControls() {
        this.grid = new ApplicationGrid<>();
        this.menuBar = new MenuBar();
        this.filterFunction = (query, t) -> t.toString().contains(query);

        var spacer = new Div();

        this.filterQuery = new TextField();
        filterQuery.setPlaceholder("Filter ...");
        filterQuery.setValueChangeMode(ValueChangeMode.LAZY);
        filterQuery.addValueChangeListener(event -> {
            try {
                grid.getListDataView().setFilter(
                    (SerializablePredicate<T>) t -> filterFunction.get(event.getValue(), t)
                );
            } catch (Exception ignored) {

            }
        });

        var controls = new HorizontalLayout();
        controls.add(menuBar, spacer, filterQuery);
        controls.setFlexGrow(1.0, spacer);
        controls.setFlexGrow(0.25, filterQuery);
        controls.setWidthFull();

        this.add(controls, grid);
        this.setPadding(false);
    }

    /**
     * Sets the filter function. The function is used to filter items in the grid when a query
     * is entered into `filterQuery`.
     *
     * @param filterFunction A function which receives the query and the object to test and should return true if
     *                       item should be shown in grid.
     */
    public void setFilterFunction(Function2<String, T, Boolean> filterFunction) {
        this.filterFunction = filterFunction;
    }

}
