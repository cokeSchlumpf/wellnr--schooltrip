package com.wellnr.schooltrip.ui.components.grid;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.wellnr.ddd.AggregateRoot;

import java.util.List;

public class ApplicationGridAndDetails<T extends AggregateRoot<?, T>> extends HorizontalLayout {

    private final ApplicationGridWithControls<T> grid;
    private final EntityDetailsControl<T> details;

    public ApplicationGridAndDetails(
        ApplicationGridWithControls<T> grid,
        EntityDetailsControl<T> details
    ) {
        this.grid = grid;
        this.details = details;

        grid.getGrid().addSelectionListener(event -> {
            if (event.getFirstSelectedItem().isPresent()) {
                details.setEntity(event.getFirstSelectedItem().get());
            } else {
                details.close();
            }
        });

        this.add(grid, details);
        this.setAlignItems(Alignment.STRETCH);
        this.setFlexGrow(1, grid);
        this.setFlexGrow(0, details);
        this.setWidthFull();

        this.setMargin(false);
        this.setPadding(false);
        this.setSizeFull();
    }

    public EntityDetailsControl<T> getDetails() {
        return details;
    }

    public ApplicationGridWithControls<T> getGrid() {
        return grid;
    }

    public void setEntities(List<T> entities) {
        // Remember the current selected student.
        var maybeSelectedEntity = this.grid
            .getGrid()
            .getSelectedItems()
            .stream()
            .findFirst();

        // Updte the grid. This will remove selection.
        this.grid.getGrid().setItems(entities);

        // Check whether previously selected student is still present, if yes, select.
        if (maybeSelectedEntity.isPresent()) {
            var selectedEntity = maybeSelectedEntity.get();

            var newSelectedEntity = entities
                .stream()
                .filter(entity -> entity.getId().equals(selectedEntity.getId()))
                .findFirst();

            if (newSelectedEntity.isPresent()) {
                this.grid.getGrid().select(newSelectedEntity.get());
            } else {
                this.details.close();
            }
        } else {
            this.details.close();
        }
    }

}
