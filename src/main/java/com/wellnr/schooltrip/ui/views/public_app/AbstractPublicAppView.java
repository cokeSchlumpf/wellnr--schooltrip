package com.wellnr.schooltrip.ui.views.public_app;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.HasDynamicTitle;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;

import java.util.Locale;
import java.util.Objects;

public abstract class AbstractPublicAppView extends Div implements HasDynamicTitle {

    protected final SchoolTripMessages i18n;

    protected final Div contentContainer;

    protected SchoolTrip schoolTrip;

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

        var switchLanguageButton = new Button(this.i18n.switchLanguage());
        switchLanguageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        switchLanguageButton.addClassName("app__public-app-view__language__button");
        switchLanguageButton.addClickListener(event -> {
            if (userSession.isGerman()) {
                userSession.setLocale(Locale.ENGLISH);
            } else {
                userSession.setLocale(Locale.GERMAN);
            }

            UI.getCurrent().getPage().reload();
        });

        var languageSettingsContainer = new Div();
        languageSettingsContainer.addClassName("app__public-app-view__language");
        languageSettingsContainer.add(switchLanguageButton);
        logoContainer.add(languageSettingsContainer);

        this.add(logoContainer);
        this.add(contentContainer);
    }

    @Override
    public String getPageTitle() {
        if (Objects.nonNull(schoolTrip)) {
            return "GaS Merzig - " + schoolTrip.getTitle();
        } else {
            return "GaS Merzig";
        }
    }
}
