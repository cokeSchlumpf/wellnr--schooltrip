package com.wellnr.schooltrip.ui;

import com.vaadin.flow.router.Route;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.application.commands.UpdateSchoolTripSettingsCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.ui.components.CommandForm;
import com.wellnr.schooltrip.ui.components.CommandFormBuilder;
import com.wellnr.schooltrip.ui.views.trips.AbstractSchoolTripView;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

@SuppressWarnings("FieldCanBeLocal")
@Route(value = "trips/:name/settings", layout = ApplicationAppLayout.class)
public class SchoolTripSettingsView extends AbstractSchoolTripView {

    private final CommandForm<MessageResult<SchoolTrip>, UpdateSchoolTripSettingsCommand> form;


    public SchoolTripSettingsView(SchoolTripCommandRunner commandRunner) {
        super(commandRunner);

        this.form = new CommandFormBuilder<>(UpdateSchoolTripSettingsCommand.class, commandRunner)
            .addVariant(
                "basePrice",
                CommandFormBuilder.FormVariant.EURO_SUFFIX,
                CommandFormBuilder.FormVariant.LINE_BREAK_AFTER
            )
            .addVariant("skiRentalPrice", CommandFormBuilder.FormVariant.EURO_SUFFIX)
            .addVariant("skiBootsRentalPrice", CommandFormBuilder.FormVariant.EURO_SUFFIX)
            .addVariant("snowboardRentalPrice", CommandFormBuilder.FormVariant.EURO_SUFFIX)
            .addVariant(
                "snowboardBootsRentalPrice",
                CommandFormBuilder.FormVariant.EURO_SUFFIX,
                CommandFormBuilder.FormVariant.LINE_BREAK_AFTER
            )
            .build();

        this.add(form);
    }

    @Override
    protected void updateView() {
        this.form.setGetInitialCommand(() -> UpdateSchoolTripSettingsCommand.apply(
            this.schoolTrip.schoolTrip().getName(),
            this.schoolTrip.schoolTrip().getSettings()
        ));
    }

}
