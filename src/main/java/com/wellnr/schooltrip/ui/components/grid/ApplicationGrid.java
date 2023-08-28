package com.wellnr.schooltrip.ui.components.grid;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.wellnr.common.functions.Function1;
import com.wellnr.common.functions.Procedure2;

import java.util.Collection;
import java.util.Collections;

public class ApplicationGrid<T> extends Grid<T> {

    public ApplicationGrid() {
        this.setAllRowsVisible(true);

        this.addThemeVariants(
            GridVariant.LUMO_ROW_STRIPES
        );
    }

    public Column<T> addActionsColumn(Function1<T, Collection<Component>> components) {
        return this
            .addComponentColumn(g -> {
                var layout = new HorizontalLayout();
                layout.add(components.get(g));
                layout.setAlignItems(FlexComponent.Alignment.CENTER);
                layout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
                return layout;
            })
            .setHeader("")
            .setTextAlign(ColumnTextAlign.END)
            .setFrozenToEnd(true);
    }

    public Column<T> addRemoveColumn(Procedure2<T, ClickEvent<Button>> onRemove, String label) {
        return addActionsColumn(item -> {
            var bttRemove = new Button(label);
            bttRemove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            bttRemove.addClickListener(event -> onRemove.run(item, event));

            return Collections.singleton(bttRemove);
        });
    }

    public Column<T> addRemoveColumn(Procedure2<T, ClickEvent<Button>> onRemove) {
        return addRemoveColumn(onRemove, "Remove");
    }
}
