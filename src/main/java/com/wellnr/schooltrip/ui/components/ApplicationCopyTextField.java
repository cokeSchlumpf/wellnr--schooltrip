package com.wellnr.schooltrip.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class ApplicationCopyTextField extends HorizontalLayout {

    public final Button button;

    public final TextField textField;

    public ApplicationCopyTextField() {
        this.textField = new TextField();
        this.textField.setAutoselect(true);

        this.button = new Button(VaadinIcon.COPY_O.create());

        /*
            var button = new Button("Copy Registration Link");
            button.addClickListener(event -> {
                UI.getCurrent().getPage().executeJs(
                    "window.copyToClipboard($0)",

                );
            });
             */

        this.add(textField, button);
        this.setFlexGrow(1, this.textField);
        this.setFlexGrow(0, this.button);
        this.setJustifyContentMode(JustifyContentMode.END);
        this.setAlignItems(Alignment.END);

        this.setWidthFull();
    }

}
