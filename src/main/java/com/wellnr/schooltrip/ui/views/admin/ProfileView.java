package com.wellnr.schooltrip.ui.views.admin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.wellnr.schooltrip.core.application.commands.UpdateRegisteredUserCommand;
import com.wellnr.schooltrip.core.application.commands.schooltrip.ResetPasswordCommand;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.LoginView;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandFormBuilder;
import com.wellnr.schooltrip.ui.components.forms.ApplicationFormBuilder;
import com.wellnr.schooltrip.ui.layout.AbstractApplicationAppView;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;
import com.wellnr.schooltrip.ui.layout.ApplicationAppView;

import java.util.Optional;

@Route(value = "profile", layout = ApplicationAppLayout.class)
public class ProfileView extends AbstractApplicationAppView implements ApplicationAppView {

    private final ApplicationCommandRunner commandRunner;

    public ProfileView(
        ApplicationCommandRunner commandRunner, ApplicationUserSession userSession
    ) {
        super(userSession);

        this.commandRunner = commandRunner;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        super.beforeEnter(event);

        if (userSession.getRegisteredUser().isEmpty()) {
            event.rerouteTo(LoginView.class);
        }

        var user = userSession.getRegisteredUser().get();

        //noinspection SwitchStatementWithTooFewBranches
        var updatePropertiesForm = new ApplicationCommandFormBuilder<>(
            UpdateRegisteredUserCommand.class,
            commandRunner,
            () -> UpdateRegisteredUserCommand.apply(
                user.getEmail(), user.getEmail(), user.getFirstName(), user.getLastName()
            )
        )
            .addVariant("oldEmail", ApplicationFormBuilder.FormVariant.HIDDEN)
            .addVariant("newEmail", ApplicationFormBuilder.FormVariant.LINE_BREAK_AFTER)
            .setLabelProvider(field -> switch (field) {
                case "newEmail" -> Optional.of("E-Mail");
                default -> Optional.empty();
            })
            .build()
            .withCompletionListener(
                e -> UI.getCurrent().navigate(ProfileView.class)
            );

        var resetPasswordForm = new ApplicationCommandFormBuilder<>(
            ResetPasswordCommand.class,
            commandRunner,
            () -> ResetPasswordCommand.apply(
                user.getEmail(), "", ""
            )
        )
            .addVariant(
                "email", ApplicationFormBuilder.FormVariant.HIDDEN
            )
            .setLabelProvider(field -> switch (field) {
                case "newPassword" -> Optional.of("New Password");
                case "passwordRepeated" -> Optional.of("Repeat New Password");
                default -> Optional.empty();
            })
            .build();

        this.removeAll();
        this.add(new H4("Profile Settings"));
        this.add(updatePropertiesForm);

        this.add(new Hr());
        this.add(resetPasswordForm);
    }
}
