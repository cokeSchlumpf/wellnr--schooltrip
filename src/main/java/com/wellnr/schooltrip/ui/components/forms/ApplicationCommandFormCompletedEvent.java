package com.wellnr.schooltrip.ui.components.forms;

import com.vaadin.flow.component.ComponentEvent;
import com.wellnr.ddd.commands.CommandResult;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import lombok.Getter;

@Getter
public class ApplicationCommandFormCompletedEvent<RESULT extends CommandResult,
        CMD extends AbstractSchoolTripCommand<RESULT>> extends ComponentEvent<ApplicationCommandForm<RESULT, CMD>> {

    private final RESULT result;

    private final CMD command;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param source the source component
     */
    public ApplicationCommandFormCompletedEvent(ApplicationCommandForm<RESULT, CMD> source, CMD cmd, RESULT result) {
        super(source, false);
        this.result = result;
        this.command = cmd;
    }

}
