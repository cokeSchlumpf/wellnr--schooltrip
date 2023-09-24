package com.wellnr.schooltrip.ui.views.admin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.Route;
import com.wellnr.schooltrip.core.application.commands.users.ListUsersCommand;
import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import com.wellnr.schooltrip.core.model.user.rbac.DomainPermissions;
import com.wellnr.schooltrip.core.model.user.rbac.DomainRole;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridAndDetails;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridWithControls;
import com.wellnr.schooltrip.ui.components.users.UserDetailsControl;
import com.wellnr.schooltrip.ui.layout.AbstractApplicationAppView;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "settings", layout = ApplicationAppLayout.class)
public class SettingsView extends AbstractApplicationAppView {

    private final SchoolTripMessages i18n;

    private final ApplicationCommandRunner commandRunner;

    private final ApplicationGridAndDetails<RegisteredUser> gridAndDetails;

    public SettingsView(ApplicationCommandRunner commandRunner, ApplicationUserSession userSession) {
        super(userSession, List.of(
            DomainPermissions.ManageApplication.apply()
        ));

        this.i18n = userSession.getMessages();
        this.commandRunner = commandRunner;

        this.gridAndDetails = new ApplicationGridAndDetails<>(new UsersGrid(), new UserDetailsControl(i18n,
            commandRunner));
        this.gridAndDetails.getDetails().addUpdatedListener(event -> refreshData());

        this.add(
            new H3(i18n.applicationSettings()),
            gridAndDetails
        );

        refreshData();
    }

    private void refreshData() {
        gridAndDetails.setEntities(
            commandRunner.run(ListUsersCommand.apply()).getData()
        );
    }

    private class UsersGrid extends ApplicationGridWithControls<RegisteredUser> {

        public UsersGrid() {
            this.getGrid()
                .addColumn(RegisteredUser::getEmail)
                .setHeader(i18n.email());

            this.getGrid()
                .addColumn(RegisteredUser::getLastName)
                .setHeader(i18n.lastName());

            this.getGrid()
                .addColumn(RegisteredUser::getFirstName)
                .setHeader(i18n.firstName());

            this.getGrid()
                .addColumn(user -> user
                    .getDomainRoles()
                    .stream()
                    .map(DomainRole::getName)
                    .collect(Collectors.joining(",\n"))
                )
                .setHeader(i18n.userRoles());

            var bttNew = this.getMenuBar().addItem(i18n.addRegisteredUser());
            bttNew.addClickListener(ignore -> UI.getCurrent().navigate(CreateUserView.class));
        }

    }

}
