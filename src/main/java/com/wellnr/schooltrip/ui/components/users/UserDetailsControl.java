package com.wellnr.schooltrip.ui.components.users;

import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.application.commands.schooltrip.ResetPasswordCommand;
import com.wellnr.schooltrip.core.application.commands.UpdateRegisteredUserCommand;
import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandForm;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandFormBuilder;
import com.wellnr.schooltrip.ui.components.forms.ApplicationFormBuilder;
import com.wellnr.schooltrip.ui.components.grid.EntityDetailsControl;

import java.util.Optional;

public class UserDetailsControl extends EntityDetailsControl<RegisteredUser> {

    ApplicationCommandForm<MessageResult<RegisteredUser>, UpdateRegisteredUserCommand> updatePropertiesForm;
    ApplicationCommandForm<MessageResult<RegisteredUser>, ResetPasswordCommand> resetPasswordForm;

    public UserDetailsControl(SchoolTripCommandRunner commandRunner) {
        super(commandRunner);

        //noinspection SwitchStatementWithTooFewBranches
        this.updatePropertiesForm = new ApplicationCommandFormBuilder<>(
            UpdateRegisteredUserCommand.class, commandRunner
        )
            .addVariant("oldEmail", ApplicationFormBuilder.FormVariant.HIDDEN)
            .addVariant("newEmail", ApplicationFormBuilder.FormVariant.LINE_BREAK_AFTER)
            .setLabelProvider(field -> switch (field) {
                case "newEmail" -> Optional.of("E-Mail");
                default -> Optional.empty();
            })
            .build();

        this.updatePropertiesForm.addCompletionListener(
            event -> fireUpdatedEvent(event.getResult().getData())
        );

        //noinspection SwitchStatementWithTooFewBranches
        this.resetPasswordForm = new ApplicationCommandFormBuilder<>(
            ResetPasswordCommand.class, commandRunner
        )
            .addVariant("email", ApplicationFormBuilder.FormVariant.HIDDEN)
            .setLabelProvider(field -> switch (field) {
                case "newPasswordRepeated" -> Optional.of("Repeat Password");
                default -> Optional.empty();
            })
            .build();

        this.resetPasswordForm.addCompletionListener(
            event -> fireUpdatedEvent(event.getResult().getData())
        );

        this.add(new H4("User Properties"));
        this.add(updatePropertiesForm);
        this.add(new Hr());
        this.add(new H4("Reset Password"));
        this.add(resetPasswordForm);
    }

    @Override
    public void setEntity(RegisteredUser entity) {
        super.setEntity(entity);

        this.updatePropertiesForm.setGetInitialCommand(() -> UpdateRegisteredUserCommand.apply(
            entity.getEmail(), entity.getEmail(), entity.getFirstName(), entity.getLastName()
        ));

        this.resetPasswordForm.setGetInitialCommand(() -> ResetPasswordCommand.apply(
            entity.getEmail(), "", ""
        ));
    }
}
