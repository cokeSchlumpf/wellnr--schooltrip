package com.wellnr.schooltrip.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.wellnr.common.Operators;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.application.commands.UpdateSchoolTripSettingsCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.views.components.CommandForm;
import com.wellnr.schooltrip.views.components.CommandFormBuilder;
import com.wellnr.schooltrip.views.layout.AbstractSchoolTripView;
import com.wellnr.schooltrip.views.layout.SchoolTripAppLayout;

import java.time.Duration;

@SuppressWarnings("FieldCanBeLocal")
@Route(value = "trips/:name/settings", layout = SchoolTripAppLayout.class)
public class SchoolTripSettingsView extends AbstractSchoolTripView {

    private final CommandForm<MessageResult<SchoolTrip>, UpdateSchoolTripSettingsCommand> form;


    public SchoolTripSettingsView(SchoolTripCommandRunner commandRunner) {
        super(false, commandRunner);

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
