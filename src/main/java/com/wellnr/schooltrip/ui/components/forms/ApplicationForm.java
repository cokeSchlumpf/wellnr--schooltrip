package com.wellnr.schooltrip.ui.components.forms;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.wellnr.common.functions.Function0;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ApplicationForm<T> extends VerticalLayout {

    private final BeanValidationBinder<T> binder;

    private Function0<T> getInitialValue;

    private final List<FormLayout> forms;

    private final Button saveButton;

    public ApplicationForm(
        BeanValidationBinder<T> binder,
        Function0<T> getInitialValue,
        List<FormLayout> forms,
        boolean withSaveButton) {

        this.binder = binder;
        this.getInitialValue = getInitialValue;
        this.forms = forms;

        this.setMargin(false);
        this.setPadding(false);

        forms.forEach(this::add);

        if (withSaveButton) {
            saveButton = new Button("Save");
            saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            this.add(saveButton);

            binder.addStatusChangeListener(
                status -> saveButton.setEnabled(!status.hasValidationErrors())
            );
        } else {
            this.saveButton = null;
        }

        this.setGetInitialValue(getInitialValue);
    }

    public Function0<T> getGetInitialValue() {
        return getInitialValue;
    }

    public void setGetInitialValue(Function0<T> getInitialValue) {
        this.getInitialValue = getInitialValue;
        var cmd = this.getInitialValue.get();

        this.binder.readBean(cmd);

        if (this.saveButton != null) {
            this.saveButton.setEnabled(true);
        }
    }

    public BeanValidationBinder<T> getBinder() {
        return binder;
    }

    public List<FormLayout> getForms() {
        return new ArrayList<>(forms);
    }

    public Button getSaveButton() {
        return saveButton;
    }

}
