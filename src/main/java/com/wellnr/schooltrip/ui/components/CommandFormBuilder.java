package com.wellnr.schooltrip.ui.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.wellnr.common.Operators;
import com.wellnr.common.functions.Function0;
import com.wellnr.ddd.commands.CommandResult;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandFormBuilder<RESULT extends CommandResult, CMD extends AbstractSchoolTripCommand<RESULT>> {

    private final Class<CMD> commandType;

    private final Function0<CMD> getInitialCommand;

    private final SchoolTripCommandRunner commandRunner;

    private final Map<Field, Set<FormVariant>> variants;

    public CommandFormBuilder(Class<CMD> commandType, SchoolTripCommandRunner commandRunner, Function0<CMD> getInitialCommand) {
        this(commandType, getInitialCommand, commandRunner, new HashMap<>());
    }

    public CommandFormBuilder(Class<CMD> commandType, SchoolTripCommandRunner commandRunner) {
        this(commandType, () -> null, commandRunner, new HashMap<>());
    }

    public CommandFormBuilder<RESULT, CMD> addVariant(Field field, FormVariant... variants) {
        var toBeAdded = Arrays.stream(variants).toList();

        if (this.variants.containsKey(field)) {
            this.variants.get(field).addAll(toBeAdded);
        } else {
            this.variants.put(field, new HashSet<>(toBeAdded));
        }

        return this;
    }

    public CommandFormBuilder<RESULT, CMD> addVariant(String field, FormVariant... variants) {
        var declaredField = Operators.suppressExceptions(() -> this.commandType.getDeclaredField(field));
        return addVariant(declaredField, variants);
    }

    public CommandForm<RESULT, CMD> build() {
        var binder = new BeanValidationBinder<>(commandType);
        var forms = new ArrayList<FormLayout>();

        var form = createForm();
        forms.add(form);

        for (var field : commandType.getDeclaredFields()) {
            if (Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            var label = getLabel(field.getName());
            var fullWidthColspan = 2;

            if (String.class.isAssignableFrom(field.getType())) {
                var input = new TextField(field.getName());
                input.setLabel(label);
                input.setValueChangeMode(ValueChangeMode.LAZY);

                binder.bind(input, field.getName());
                form.add(input);

                if (hasVariant(field, FormVariant.FULL_WIDTH)) {
                    form.setColspan(input, fullWidthColspan);
                }
            } else if (Boolean.class.isAssignableFrom(field.getType())) {
                var input = new Checkbox();
                input.setLabel(label);

                binder.bind(input, field.getName());
                form.add(input);

                if (hasVariant(field, FormVariant.FULL_WIDTH)) {
                    form.setColspan(input, fullWidthColspan);
                }
            } else if (LocalDate.class.isAssignableFrom(field.getType())) {
                var input = new DatePicker();
                input.setLabel(label);

                binder.bind(input, field.getName());
                form.add(input);

                if (hasVariant(field, FormVariant.FULL_WIDTH)) {
                    form.setColspan(input, fullWidthColspan);
                }
            } else if (
                Integer.class.isAssignableFrom(field.getType()) ||
                    int.class.isAssignableFrom(field.getType())
            ) {
                var input = new IntegerField();
                input.setLabel(label);
                input.setValueChangeMode(ValueChangeMode.LAZY);

                binder.bind(input, field.getName());
                form.add(input);

                if (hasVariant(field, FormVariant.FULL_WIDTH)) {
                    form.setColspan(input, fullWidthColspan);
                }
            } else if (
                Double.class.isAssignableFrom(field.getType()) ||
                    double.class.isAssignableFrom(field.getType())
            ) {
                var input = new NumberField();
                input.setLabel(label);
                input.setValueChangeMode(ValueChangeMode.LAZY);

                if (hasVariant(field, FormVariant.EURO_SUFFIX)) {
                    input.setSuffixComponent(new Div(new Text("€")));
                }

                binder.bind(input, field.getName());
                form.add(input);

                if (hasVariant(field, FormVariant.FULL_WIDTH)) {
                    form.setColspan(input, fullWidthColspan);
                }
            } else if (LocalDateTime.class.isAssignableFrom(field.getType())) {
                var input = new DateTimePicker();
                input.setLabel(label);

                binder.bind(input, field.getName());
                form.add(input);
            } else if (field.getType().isEnum()) {
                List<Object> values = Arrays
                    .stream(field.getType().getEnumConstants())
                    .map(obj -> (Object) obj)
                    .toList();

                if (hasVariant(field, FormVariant.RADIO_BUTTONS)) {
                    var input = new RadioButtonGroup<>();
                    input.setLabel(label);
                    input.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
                    input.setItems(values);
                    input.setItemLabelGenerator(Object::toString);

                    binder.bind(input, field.getName());
                    form.add(input);

                    if (hasVariant(field, FormVariant.FULL_WIDTH)) {
                        form.setColspan(input, fullWidthColspan);
                    }
                } else {
                    var input = new Select<>();
                    input.setLabel(label);
                    input.setItems(values);
                    input.setItemLabelGenerator(Object::toString);

                    binder.bind(input, field.getName());
                    form.add(input);

                    if (hasVariant(field, FormVariant.FULL_WIDTH)) {
                        form.setColspan(input, fullWidthColspan);
                    }
                }
            } else {
                throw new IllegalArgumentException(String.format(
                    "Can't map type `%s` to a component. (Field: `%s`)",
                    field.getType(), field.getName()
                ));
            }

            if (hasVariant(field, FormVariant.LINE_BREAK_AFTER)) {
                form = createForm();
                forms.add(form);
            }
        }

        var save = new Button("Save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            var cmd = this.getInitialCommand.get();
            Operators.suppressExceptions(() -> binder.writeBean(cmd));

            System.out.println(commandRunner.run(cmd));
        });
        binder.addStatusChangeListener(
            status -> save.setEnabled(!status.hasValidationErrors())
        );

        return new CommandForm<>(
            binder,
            this.getInitialCommand,
            commandRunner,
            forms
        );
    }

    private FormLayout createForm() {
        var form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        return form;
    }

    private String getLabel(String fieldName) {
        return Operators.camelCaseToHumanReadable(fieldName);
    }

    private boolean hasVariant(Field field, FormVariant variant) {
        if (this.variants.containsKey(field)) {
            return this.variants.get(field).contains(variant);
        } else {
            return false;
        }
    }

    public enum FormVariant {
        /**
         * Mark a field to have a line break in the responsive layout after the component.
         */
        LINE_BREAK_AFTER,

        /**
         * Use radio buttons instead of select field for enum types.
         */
        RADIO_BUTTONS,

        /**
         * Use to mark a field to have € suffix.
         */
        EURO_SUFFIX,

        /**
         * Use this to mark a field to have full width.
         */
        FULL_WIDTH
    }

}
