package com.wellnr.schooltrip.ui.components.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.wellnr.common.functions.Function0;
import com.wellnr.common.functions.Function1;
import com.wellnr.common.markup.Tuple2;
import com.wellnr.ddd.commands.CommandResult;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class ApplicationCommandFormBuilder<RESULT extends CommandResult, CMD extends AbstractSchoolTripCommand<RESULT>>
    extends ApplicationFormBuilder<CMD> {


    private final ApplicationCommandRunner commandRunner;

    public ApplicationCommandFormBuilder(
        Class<CMD> commandType,
        ApplicationCommandRunner commandRunner,
        Function0<CMD> getInitialCommand
    ) {

        super(commandType, getInitialCommand);
        this.commandRunner = commandRunner;
        this.withSaveButton(true);
    }

    public ApplicationCommandFormBuilder(Class<CMD> commandType, ApplicationCommandRunner commandRunner) {
        this(commandType, commandRunner, () -> null);
    }

    @Override
    public ApplicationCommandFormBuilder<RESULT, CMD> addCustomComponent(
        Field field, Function1<BeanValidationBinder<CMD>, Component> componentFactory
    ) {
        super.addCustomComponent(field, componentFactory);
        return this;
    }

    @Override
    public ApplicationCommandFormBuilder<RESULT, CMD> addCustomComponent(
        String field, Function1<BeanValidationBinder<CMD>, Component> componentFactory
    ) {

        super.addCustomComponent(field, componentFactory);
        return this;
    }

    @Override
    public ApplicationCommandFormBuilder<RESULT, CMD> addVariant(Field field, FormVariant... variants) {
        super.addVariant(field, variants);
        return this;
    }

    @Override
    public ApplicationCommandFormBuilder<RESULT, CMD> addVariant(String field, FormVariant... variants) {
        super.addVariant(field, variants);
        return this;
    }

    @Override
    public ApplicationCommandForm<RESULT, CMD> build() {
        var form = super.build();

        return new ApplicationCommandForm<>(
            form.getBinder(),
            form.getGetInitialValue(),
            this.commandRunner,
            form.getForms(),
            getLabel("Save")
        );
    }

    @Override
    public ApplicationCommandFormBuilder<RESULT, CMD> setFieldPossibleValues(Field field,
                                                                             List<Tuple2<String, String>> possibleValues) {
        super.setFieldPossibleValues(field, possibleValues);
        return this;
    }

    @Override
    public ApplicationCommandFormBuilder<RESULT, CMD> setFieldPossibleValues(String field, List<Tuple2<String,
        String>> possibleValues) {
        super.setFieldPossibleValues(field, possibleValues);
        return this;
    }

    @Override
    public ApplicationCommandFormBuilder<RESULT, CMD> setI18nMessages(Object i18nMessages) {
        super.setI18nMessages(i18nMessages);
        return this;
    }

    @Override
    public ApplicationCommandFormBuilder<RESULT, CMD> setLabelProvider(Function1<String, Optional<String>> labelProvider) {
        super.setLabelProvider(labelProvider);
        return this;
    }

    @Override
    public ApplicationCommandFormBuilder<RESULT, CMD> withTitle(String title) {
        super.withTitle(title);
        return this;
    }
}
