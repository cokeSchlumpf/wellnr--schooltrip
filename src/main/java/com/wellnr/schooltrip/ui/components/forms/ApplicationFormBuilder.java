package com.wellnr.schooltrip.ui.components.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.wellnr.common.Operators;
import com.wellnr.common.functions.Function0;
import com.wellnr.common.functions.Function1;
import com.wellnr.common.markup.Tuple2;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationFormBuilder<T> {

    private final Class<T> valueType;

    private final Function0<T> getInitialValue;

    private final Map<Field, Set<FormVariant>> variants;

    private final Map<Field, Function1<BeanValidationBinder<T>, Component>> customComponents;

    private Function1<String, Optional<String>> labelProvider;

    private boolean withSaveButton;

    private String title;

    public ApplicationFormBuilder(
        Class<T> valueType,
        Function0<T> getInitialValue
    ) {

        this(
            valueType, getInitialValue, new HashMap<>(), new HashMap<>(),
            s -> Optional.empty(), false, null
        );
    }

    public ApplicationFormBuilder(Class<T> valueType) {
        this(valueType, () -> null);
    }

    public ApplicationFormBuilder<T> addVariant(Field field, FormVariant... variants) {
        var toBeAdded = Arrays.stream(variants).toList();

        if (this.variants.containsKey(field)) {
            this.variants.get(field).addAll(toBeAdded);
        } else {
            this.variants.put(field, new HashSet<>(toBeAdded));
        }

        return this;
    }

    public ApplicationFormBuilder<T> addVariant(String field, FormVariant... variants) {
        var declaredField = Operators.suppressExceptions(() -> this.valueType.getDeclaredField(field));
        return addVariant(declaredField, variants);
    }

    public ApplicationFormBuilder<T> addCustomComponent(Field field,
                                                        Function1<BeanValidationBinder<T>, Component> componentFactory) {
        this.customComponents.put(field, Objects.requireNonNull(componentFactory));
        return this;
    }

    public ApplicationFormBuilder<T> addCustomComponent(String field,
                                                        Function1<BeanValidationBinder<T>, Component> componentFactory) {
        var declaredField = Operators.suppressExceptions(() -> this.valueType.getDeclaredField(field));
        return addCustomComponent(declaredField, componentFactory);
    }

    /**
     * Spcifies possible values for a (text) field. The field will then be represented as a select box.
     *
     * @param field The field which the values should be constrained.
     * @param possibleValues A list of possible values. 1st parameter is the value, 2nd the label.
     * @return This application form builder.
     */
    public ApplicationFormBuilder<T> setFieldPossibleValues(
        Field field, List<Tuple2<String, String>> possibleValues) {

        return addCustomComponent(field, binder -> {
            var valuesMap = possibleValues
                .stream()
                .collect(Collectors.toMap(Tuple2::get_1, Tuple2::get_2));

            var input = new Select<String>();
            input.setLabel(this.getLabel(field.getName()));
            input.setItems(possibleValues.stream().map(Tuple2::get_2).toList());
            input.setItemLabelGenerator(valuesMap::get);

            binder.bind(input, field.getName());

            return input;
        });
    }

    /**
     * Spcifies possible values for a (text) field. The field will then be represented as a select box.
     *
     * @param field The field which the values should be constrained.
     * @param possibleValues A list of possible values. 1st parameter is the value, 2nd the label.
     * @return This application form builder.
     */
    public ApplicationFormBuilder<T> setFieldPossibleValues(
        String field, List<Tuple2<String, String>> possibleValues) {
        return setFieldPossibleValues(
            Operators.suppressExceptions(() -> valueType.getDeclaredField(field)), possibleValues
        );
    }

    /**
     * Specifies the label provider for the controls. The function will be called with the field name
     * of the class. It may return a label. If Optional.empty() is returned, the field name will
     * be humanized.
     *
     * @param labelProvider A function to provide labels.
     * @return The form builder.
     */
    public ApplicationFormBuilder<T> setLabelProvider(Function1<String, Optional<String>> labelProvider) {
        this.labelProvider = labelProvider;
        return this;
    }

    public ApplicationFormBuilder<T> withSaveButton(boolean withSaveButton) {
        this.withSaveButton = withSaveButton;
        return this;
    }

    public ApplicationFormBuilder<T> withSaveButton() {
        return withSaveButton(true);
    }

    public ApplicationFormBuilder<T> withTitle(String title) {
        this.title = title;
        return this;
    }

    public ApplicationForm<T> build() {
        var binder = new BeanValidationBinder<>(valueType);
        var forms = new ArrayList<FormLayout>();
        var fullWidthColspan = 2;

        var form = createForm();

        if (Objects.nonNull(this.title)) {
            var title = new H4(this.title);
            form.add(title);
            form.setColspan(title, fullWidthColspan);
        }

        forms.add(form);

        for (var field : valueType.getDeclaredFields()) {
            if (Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            if (hasVariant(field, FormVariant.HIDDEN)) {
                continue;
            }

            var label = getLabel(field.getName());

            if (customComponents.containsKey(field)) {
                var component = customComponents.get(field).get(binder);

                form.add(component);

                if (hasVariant(field, FormVariant.FULL_WIDTH)) {
                    form.setColspan(component, fullWidthColspan);
                }
            } else if (String.class.isAssignableFrom(field.getType())) {
                if (field.getName().toLowerCase().contains("password") || field.getName().toLowerCase().contains("secret")) {
                    var input = new PasswordField(field.getName());
                    input.setLabel(label);
                    input.setValueChangeMode(ValueChangeMode.LAZY);

                    binder.bind(input, field.getName());
                    form.add(input);

                    if (hasVariant(field, FormVariant.FULL_WIDTH)) {
                        form.setColspan(input, fullWidthColspan);
                    }
                } else {
                    var input = new TextField(field.getName());
                    input.setLabel(label);
                    input.setValueChangeMode(ValueChangeMode.LAZY);

                    binder.bind(input, field.getName());
                    form.add(input);

                    if (hasVariant(field, FormVariant.FULL_WIDTH)) {
                        form.setColspan(input, fullWidthColspan);
                    }
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

                ItemLabelGenerator<Object> labels = obj -> Operators.ignoreExceptionsWithDefault(
                    () -> {
                        var f = obj.getClass().getDeclaredField("value");
                        f.setAccessible(true);
                        return f.get(obj).toString();
                    },
                    obj.toString()
                );

                if (hasVariant(field, FormVariant.RADIO_BUTTONS)) {
                    var input = new RadioButtonGroup<>();
                    input.setLabel(label);
                    input.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
                    input.setItems(values);
                    input.setItemLabelGenerator(labels);

                    binder.bind(input, field.getName());
                    form.add(input);

                    if (hasVariant(field, FormVariant.FULL_WIDTH)) {
                        form.setColspan(input, fullWidthColspan);
                    }
                } else {
                    var input = new Select<>();
                    input.setLabel(label);
                    input.setItems(values);
                    input.setItemLabelGenerator(labels);

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

        return new ApplicationForm<>(
            binder,
            getInitialValue,
            forms,
            withSaveButton
        );
    }

    private FormLayout createForm() {
        var form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        return form;
    }

    private String getLabel(String fieldName) {
        return labelProvider.get(fieldName).orElse(Operators.camelCaseToHumanReadable(fieldName));
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
        FULL_WIDTH,

        /**
         * Use this to mark a field to be hidden.
         */
        HIDDEN
    }

}
