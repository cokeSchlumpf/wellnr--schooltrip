package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.wellnr.common.markup.Nothing;
import com.wellnr.common.markup.Tuple2;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.application.commands.schooltrip.AddSchoolTripManagerCommand;
import com.wellnr.schooltrip.core.application.commands.schooltrip.RemoveSchoolTripManagerCommand;
import com.wellnr.schooltrip.core.application.commands.schooltrip.UpdateSchoolTripSettingsCommand;
import com.wellnr.schooltrip.core.application.commands.users.ListUsersCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import com.wellnr.schooltrip.core.model.user.RegisteredUserId;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.ApplicationCard;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandForm;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandFormBuilder;
import com.wellnr.schooltrip.ui.components.forms.ApplicationFormBuilder;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGrid;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
@Route(value = "trips/:name/settings", layout = ApplicationAppLayout.class)
public class SchoolTripSettingsView extends AbstractSchoolTripView {

    private final ApplicationCommandForm<MessageResult<SchoolTrip>, UpdateSchoolTripSettingsCommand> settingsForm;

    private final ApplicationCommandForm<MessageResult<Nothing>, AddSchoolTripManagerCommand> addManagerForm;

    private final ManagersGrid managers;

    public SchoolTripSettingsView(
        ApplicationCommandRunner commandRunner, ApplicationUserSession userSession
    ) {
        super(commandRunner, userSession);

        this.settingsForm = new ApplicationCommandFormBuilder<>(UpdateSchoolTripSettingsCommand.class, commandRunner)
            .addVariant(
                "basePrice",
                ApplicationCommandFormBuilder.FormVariant.EURO_SUFFIX,
                ApplicationCommandFormBuilder.FormVariant.LINE_BREAK_AFTER
            )
            .addVariant("skiRentalPrice", ApplicationCommandFormBuilder.FormVariant.EURO_SUFFIX)
            .addVariant("skiBootsRentalPrice", ApplicationCommandFormBuilder.FormVariant.EURO_SUFFIX)
            .addVariant("snowboardRentalPrice", ApplicationCommandFormBuilder.FormVariant.EURO_SUFFIX)
            .addVariant(
                "snowboardBootsRentalPrice",
                ApplicationCommandFormBuilder.FormVariant.EURO_SUFFIX,
                ApplicationCommandFormBuilder.FormVariant.LINE_BREAK_AFTER
            )
            .build();

        this.settingsForm.setMaxWidth("640px");
        this.settingsForm.addCompletionListener(event -> this.reload());

        this.managers = new ManagersGrid();

        var users = commandRunner
            .run(ListUsersCommand.apply())
            .getData();

        this.addManagerForm = new ApplicationCommandFormBuilder<>(AddSchoolTripManagerCommand.class, commandRunner)
            .addVariant("schoolTrip", ApplicationFormBuilder.FormVariant.HIDDEN)
            .addVariant("email", ApplicationFormBuilder.FormVariant.FULL_WIDTH)
            .setFieldPossibleValues("email", users
                .stream()
                .map(user -> Tuple2.apply(user.getEmail(), user.getName()))
                .toList()
            )
            .withTitle("Add Manager")
            .build();
        this.addManagerForm.setMaxWidth("480px");
        this.addManagerForm.addCompletionListener(event -> this.reload());

        var addManagerCard = new ApplicationCard();
        addManagerCard.add(this.addManagerForm);

        this.add(new H3("Trip Settings"));
        this.add(settingsForm);

        if (userSession.getPermissions().isCanManageSchoolTrips()) {
            this.add(new Hr());
            this.add(new H3("Trip Managers"));
            this.add(managers);
            this.add(addManagerCard);
        }
    }

    public static RouteParameters getRouteParameters(String name) {
        return new RouteParameters(
            new RouteParam("name", name)
        );
    }

    @Override
    protected void updateView() {

        this.settingsForm.setGetInitialCommand(() -> UpdateSchoolTripSettingsCommand.apply(
            this.schoolTrip.schoolTrip().getName(),
            this.schoolTrip.schoolTrip().getSettings()
        ));

        this.addManagerForm.setGetInitialCommand(() -> AddSchoolTripManagerCommand.apply(
            new SchoolTripId(this.schoolTrip.schoolTrip().getId()),
            ""
        ));

        this.managers.setItems(this.schoolTrip.managers());
    }

    private class ManagersGrid extends ApplicationGrid<RegisteredUser> {

        public ManagersGrid() {
            var lastNameColumn = this
                .addColumn(RegisteredUser::getLastName)
                .setHeader("Last Name");

            var firstNameColumn = this
                .addColumn(RegisteredUser::getFirstName)
                .setHeader("First Name");

            this
                .addRemoveColumn((user, event) -> {
                    var result = commandRunner.runAndNotify(RemoveSchoolTripManagerCommand.apply(
                        new SchoolTripId(schoolTrip.schoolTrip().getId()),
                        new RegisteredUserId(user.getId())
                    ));

                    result.ifPresent(r -> reload());
                });

            this.setMultiSort(true, true);
            this.sort(List.of(
                new GridSortOrder<>(lastNameColumn, SortDirection.ASCENDING),
                new GridSortOrder<>(firstNameColumn, SortDirection.ASCENDING)
            ));
        }

    }

}
