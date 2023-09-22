package com.wellnr.schooltrip.ui.layout;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;

public class AbstractPublicAppView extends Div {

    protected final SchoolTripMessages i18n;

    protected final Div contentContainer;

    protected AbstractPublicAppView(
        ApplicationUserSession userSession
    ) {
        this.i18n = userSession.getMessages();

        this.addClassName("app__public-app-view");

        var logo = new Image("images/logo.png", "Ski- und Snowboard-Freizeit des GaS Merzig.");
        logo.addClassName("app__public-app-view__logo__img");

        var logoContainer = new Div();
        logoContainer.addClassName("app__public-app-view__logo");
        logoContainer.add(logo);

        this.contentContainer = new Div();
        contentContainer.addClassName("app__public-app-view__content");

        this.add(logoContainer);
        this.add(contentContainer);
    }

}
