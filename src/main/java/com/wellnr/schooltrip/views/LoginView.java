package com.wellnr.schooltrip.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.LoginCommand;
import com.wellnr.schooltrip.core.model.user.AnonymousUser;
import com.wellnr.schooltrip.infrastructure.UserSession;

@Route("")
@PageTitle("Login | Vaadin CRM")
public class LoginView extends VerticalLayout {

    public LoginView(SchoolTripDomainRegistry domainRegistry, UserSession session) {
        var i18n = LoginI18n.createDefault();
        i18n.getErrorMessage().setTitle(domainRegistry.getMessages().incorrectUsernameOrPassword());
        i18n.getErrorMessage().setMessage(domainRegistry.getMessages().loginFailed());
        i18n.getForm().setUsername(domainRegistry.getMessages().username());
        i18n.getForm().setPassword(domainRegistry.getMessages().password());
        i18n.getForm().setSubmit(domainRegistry.getMessages().login());

        var loginForm = new LoginForm();
        loginForm.addLoginListener(event -> {
            var result = LoginCommand
                .apply(event.getUsername(), event.getPassword())
                .run(AnonymousUser.apply(), domainRegistry);

            if (result.getData().isFailure()) {
                loginForm.setEnabled(true);
                loginForm.setError(true);
            } else {
                session.setRegisteredUser(result.getData().getValue());
                UI.getCurrent().navigate(SchoolTripsView.class);
            }
        });

        this.setSizeFull();
        this.setAlignItems(Alignment.CENTER);
        this.setJustifyContentMode(JustifyContentMode.CENTER);
        this.add(loginForm);
    }

}
