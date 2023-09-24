package com.wellnr.schooltrip.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.wellnr.common.Operators;
import com.wellnr.ddd.DomainException;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;

public class DomainExceptionView extends Div implements HasErrorParameter<Exception> {

    private final SchoolTripMessages i18n;

    private final Div errorMessage;

    public DomainExceptionView(ApplicationUserSession userSession) {
        this.i18n = userSession.getMessages();

        var content = new Div();
        content.addClassName("app__domain-exception-view__content");

        var oops = new H1("OOOPS!");
        oops.addClassName("app__domain-exception-view__oops");

        this.errorMessage = new Div();
        errorMessage.addClassName("app__domain-exception-view__error-message");

        content.add(oops, errorMessage);
        this.add(content);
        this.addClassName("app__domain-exception-view");
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter) {
        var domainException = Operators.hasCause(parameter.getException(), DomainException.class);

        if (domainException.isPresent()) {
            this.errorMessage.setText(domainException.get().getStatus() + " - " + domainException.get()
                .getUserMessage(i18n));
            return domainException.get().getStatus();
        } else {
            parameter.getException().printStackTrace();
            this.errorMessage.setText("500 - " + i18n.internalServerError());
            return 500;
        }
    }

}
