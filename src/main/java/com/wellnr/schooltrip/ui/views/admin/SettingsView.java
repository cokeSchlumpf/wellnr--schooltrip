package com.wellnr.schooltrip.ui.views.admin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.Route;
import com.wellnr.schooltrip.core.application.commands.users.ListUsersCommand;
import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import com.wellnr.schooltrip.core.model.user.rbac.DomainPermissions;
import com.wellnr.schooltrip.core.model.user.rbac.DomainRole;
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

    private final ApplicationCommandRunner commandRunner;

    private final ApplicationGridAndDetails<RegisteredUser> gridAndDetails;

    public SettingsView(ApplicationCommandRunner commandRunner, ApplicationUserSession userSession) {
        super(userSession, List.of(
            DomainPermissions.ManageApplication.apply()
        ));

        this.commandRunner = commandRunner;

        this.gridAndDetails = new ApplicationGridAndDetails<>(new UsersGrid(), new UserDetailsControl(commandRunner));
        this.gridAndDetails.getDetails().addUpdatedListener(event -> refreshData());

        this.add(
            new H3("Application Settings"),
            gridAndDetails
        );

        refreshData();
    }

    private void refreshData() {
        gridAndDetails.setEntities(
            commandRunner.run(ListUsersCommand.apply()).getData()
        );
    }

    private static class UsersGrid extends ApplicationGridWithControls<RegisteredUser> {

        public UsersGrid() {
            this.getGrid()
                .addColumn(RegisteredUser::getEmail)
                .setHeader("E-Mail");

            this.getGrid()
                .addColumn(RegisteredUser::getLastName)
                .setHeader("Last Name");

            this.getGrid()
                .addColumn(RegisteredUser::getFirstName)
                .setHeader("First Name");

            this.getGrid()
                .addColumn(user -> user
                    .getDomainRoles()
                    .stream()
                    .map(DomainRole::getName)
                    .collect(Collectors.joining(",\n"))
                )
                .setHeader("Roles");

            var bttNew = this.getMenuBar().addItem("New user");
            bttNew.addClickListener(ignore -> UI.getCurrent().navigate(CreateUserView.class));
        }

    }

}
