package com.wellnr.schooltrip.ui.components.grid;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;

public class EntityDetailsControl<T> extends VerticalLayout {

    protected final ApplicationCommandRunner commandRunner;

    protected T entity;

    protected EntityDetailsControl(ApplicationCommandRunner commandRunner) {
        this.commandRunner = commandRunner;

        setWidth("800px");
        setMaxWidth("800");
        setVisible(false);
    }

    @SuppressWarnings("unchecked")
    public Registration addUpdatedListener(
        ComponentEventListener<EntityDetailsUpdatedEvent<T>> listener
    ) {

        var dummyEvent = new EntityDetailsUpdatedEvent<T>(null, this, true);
        return addListener((Class<EntityDetailsUpdatedEvent<T>>) dummyEvent.getClass(), listener);
    }

    /**
     * Hide the control.
     */
    public void close() {
        this.setVisible(false);
    }

    public void fireUpdatedEvent(T updatedEntity) {
        fireEvent(new EntityDetailsUpdatedEvent<>(entity, this, true));
    }

    /**
     * Set the current entity in the control.
     *
     * @param entity The entity.
     */
    public void setEntity(T entity) {
        this.entity = entity;
        this.setVisible(true);
    }

}
