package com.wellnr.schooltrip.ui.views.admin;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.wellnr.schooltrip.core.application.commands.users.RegisterUserCommand;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.ApplicationContentContainer;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandFormBuilder;
import com.wellnr.schooltrip.ui.layout.AbstractApplicationAppView;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;
import com.wellnr.schooltrip.ui.layout.ApplicationAppView;

import java.util.Optional;

@Route(value = "admin/add-user", layout = ApplicationAppLayout.class)
public class CreateUserView extends AbstractApplicationAppView implements ApplicationAppView {

    public CreateUserView(ApplicationCommandRunner commandRunner, ApplicationUserSession userSession) {
        super(userSession);
        var i18n = userSession.getMessages();

        //noinspection SwitchStatementWithTooFewBranches
        var form = new ApplicationCommandFormBuilder<>(
            RegisterUserCommand.class,
            commandRunner,
            () -> RegisterUserCommand.apply("", "", "", "", false)
        )
            .setLabelProvider(field -> switch (field) {
                case "admin" -> Optional.of(i18n.userShouldBeAdmin());
                default -> Optional.empty();
            })
            .build();

        form.addCompletionListener(event -> UI.getCurrent().navigate(
            SettingsView.class
        ));

        this.add(
            new H3(
                new RouterLink(i18n.settings(), SettingsView.class),
                new Text(" » " + i18n.addRegisteredUser())
            ),

            new ApplicationContentContainer(
                form
            )
        );
    }

}
