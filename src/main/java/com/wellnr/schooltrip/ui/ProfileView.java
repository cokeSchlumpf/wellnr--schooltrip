package com.wellnr.schooltrip.ui;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.layout.AbstractApplicationAppView;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;
import com.wellnr.schooltrip.ui.layout.ApplicationAppView;

@Route(value = "profile", layout = ApplicationAppLayout.class)
public class ProfileView extends AbstractApplicationAppView implements ApplicationAppView {

    public ProfileView(
        ApplicationCommandRunner commandRunner, ApplicationUserSession userSession
    ) {
        super(userSession);

        this.add(new Span("Profile View"));
    }

}
