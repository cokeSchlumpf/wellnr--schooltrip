package com.wellnr.schooltrip.ui;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.wellnr.schooltrip.core.model.schooltrip.exceptions.SchoolTripNotFoundException;

public class DomainExceptionView extends VerticalLayout implements HasErrorParameter<SchoolTripNotFoundException> {

    private final Paragraph errorMessage;

    public DomainExceptionView() {
        this.errorMessage = new Paragraph();
        this.add(errorMessage);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<SchoolTripNotFoundException> parameter) {
        this.errorMessage.setText(parameter.getException().getMessage());
        return parameter.getException().getStatus();
    }

}
