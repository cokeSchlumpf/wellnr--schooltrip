package com.wellnr.schooltrip.infrastructure;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;

@Theme("app")
public class ApplicationAppShellConfigurator implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        AppShellConfigurator.super.configurePage(settings);

    }
}
