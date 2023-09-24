package com.wellnr.schooltrip.ui.components.forms;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.shared.Registration;
import com.wellnr.common.Operators;
import com.wellnr.common.functions.Function0;
import com.wellnr.ddd.commands.CommandResult;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.ui.components.ApplicationNotifications;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ApplicationCommandForm<RESULT extends CommandResult, CMD extends AbstractSchoolTripCommand<RESULT>> extends ApplicationForm<CMD> {

    public ApplicationCommandForm(
        BeanValidationBinder<CMD> binder,
        Function0<CMD> getInitialCommand,
        ApplicationCommandRunner commandRunner,
        List<FormLayout> forms,
        String saveButtonLabel) {

        super(binder, getInitialCommand, forms, true, saveButtonLabel);

        this.getSaveButton().addClickListener(event -> {
            var cmd = super.getGetInitialValue().get();
            Operators.suppressExceptions(() -> binder.writeBean(cmd));

            try {
                var result = commandRunner.run(cmd);

                if (result instanceof MessageResult<?> msg) {
                    ApplicationNotifications.success(msg.getMessage());
                }

                fireEvent(new ApplicationCommandFormCompletedEvent<>(this, result));
            } catch (Exception ex) {
                ApplicationNotifications.error(ex.getMessage());
                log.warn("Exception occurred while executing command `{}`.", cmd, ex);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public Registration addCompletionListener(
        ComponentEventListener<ApplicationCommandFormCompletedEvent<RESULT, CMD>> listener) {

        var dummy = new ApplicationCommandFormCompletedEvent<RESULT, CMD>(this, null);
        return addListener((Class<ApplicationCommandFormCompletedEvent<RESULT, CMD>>) dummy.getClass(), listener);
    }

    public void setGetInitialCommand(Function0<CMD> getInitialCommand) {
        super.setGetInitialValue(getInitialCommand);
    }

    public ApplicationCommandForm<RESULT, CMD> withCompletionListener(ComponentEventListener<ApplicationCommandFormCompletedEvent<RESULT, CMD>> listener) {
        this.addCompletionListener(listener);
        return this;
    }

}
