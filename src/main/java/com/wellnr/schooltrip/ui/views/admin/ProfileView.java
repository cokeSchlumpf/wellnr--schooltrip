package com.wellnr.schooltrip.ui.views.admin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.wellnr.common.markup.Tuple2;
import com.wellnr.schooltrip.core.application.commands.UpdateRegisteredUserCommand;
import com.wellnr.schooltrip.core.application.commands.schooltrip.ResetPasswordCommand;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.LoginView;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandFormBuilder;
import com.wellnr.schooltrip.ui.components.forms.ApplicationFormBuilder;
import com.wellnr.schooltrip.ui.layout.AbstractApplicationAppView;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;
import com.wellnr.schooltrip.ui.layout.ApplicationAppView;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Route(value = "profile", layout = ApplicationAppLayout.class)
public class ProfileView extends AbstractApplicationAppView implements ApplicationAppView {

    private final SchoolTripMessages i18n;

    private final ApplicationCommandRunner commandRunner;

    public ProfileView(
        ApplicationCommandRunner commandRunner, ApplicationUserSession userSession
    ) {
        super(userSession);

        this.i18n = userSession.getMessages();
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
                user.getEmail(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getPreferredLocale().map(Locale::toLanguageTag).orElse("")
            )
        )
            .addVariant("oldEmail", ApplicationFormBuilder.FormVariant.HIDDEN)
            .addVariant("newEmail", ApplicationFormBuilder.FormVariant.LINE_BREAK_AFTER)
            .setLabelProvider(field -> switch (field) {
                case "newEmail" -> Optional.of(i18n.email());
                default -> Optional.empty();
            })
            .setFieldPossibleValues("preferredLocale", List.of(
                Tuple2.apply("", i18n.noPreference()),
                Tuple2.apply(Locale.ENGLISH.toLanguageTag(), i18n.english()),
                Tuple2.apply(Locale.GERMAN.toLanguageTag(), i18n.german())
            ))
            .setI18nMessages(i18n)
            .build()
            .withCompletionListener(
                e -> {
                    e.getResult().getData().getPreferredLocale().ifPresent(userSession::setLocale);
                    UI.getCurrent().navigate(ProfileView.class);
                }
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
                case "newPassword" -> Optional.of(i18n.newPassword());
                case "newPasswordRepeated" -> Optional.of(i18n.repeatNewPassword());
                default -> Optional.empty();
            })
            .setI18nMessages(i18n)
            .build();

        this.removeAll();
        this.add(new H4(i18n.profileSettings()));
        this.add(updatePropertiesForm);

        this.add(new Hr());
        this.add(resetPasswordForm);
    }
}
