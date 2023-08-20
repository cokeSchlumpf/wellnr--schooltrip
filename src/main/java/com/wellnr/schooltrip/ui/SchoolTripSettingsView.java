package com.wellnr.schooltrip.ui;

import com.vaadin.flow.router.Route;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.application.commands.UpdateSchoolTripSettingsCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandForm;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandFormBuilder;
import com.wellnr.schooltrip.ui.views.trips.AbstractSchoolTripView;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

@SuppressWarnings("FieldCanBeLocal")
@Route(value = "trips/:name/settings", layout = ApplicationAppLayout.class)
public class SchoolTripSettingsView extends AbstractSchoolTripView {

    private final ApplicationCommandForm<MessageResult<SchoolTrip>, UpdateSchoolTripSettingsCommand> form;


    public SchoolTripSettingsView(SchoolTripCommandRunner commandRunner) {
        super(commandRunner);

        this.form = new ApplicationCommandFormBuilder<>(UpdateSchoolTripSettingsCommand.class, commandRunner)
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
