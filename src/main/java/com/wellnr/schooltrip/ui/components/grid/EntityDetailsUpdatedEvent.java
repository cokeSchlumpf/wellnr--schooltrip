package com.wellnr.schooltrip.ui.components.grid;

import com.vaadin.flow.component.ComponentEvent;

public class EntityDetailsUpdatedEvent<T> extends ComponentEvent<EntityDetailsControl<T>> {

    T entity;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param entity     the entity which has been updated.
     * @param source     the source component
     * @param fromClient <code>true</code> if the event originated from the client
     *                   side, <code>false</code> otherwise
     */
    public EntityDetailsUpdatedEvent(T entity, EntityDetailsControl<T> source, boolean fromClient) {
        super(source, fromClient);
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }

}
