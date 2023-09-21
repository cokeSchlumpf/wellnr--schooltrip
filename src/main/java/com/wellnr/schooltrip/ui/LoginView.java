package com.wellnr.schooltrip.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinResponse;
import com.wellnr.schooltrip.SchooltripApplication;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.infrastructure.ApplicationRESTAPIController;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.views.trips.SchoolTripsView;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Route("")
@PageTitle("wellnr SchoolTrips")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final ApplicationUserSession userSession;

    public LoginView(ApplicationRESTAPIController authController, SchoolTripDomainRegistry domainRegistry,
                     ApplicationUserSession session) {
        this.userSession = session;

        var i18n = LoginI18n.createDefault();
        i18n.getErrorMessage().setTitle(domainRegistry.getMessages().incorrectUsernameOrPassword());
        i18n.getErrorMessage().setMessage(domainRegistry.getMessages().loginFailed());
        i18n.getForm().setUsername(domainRegistry.getMessages().username());
        i18n.getForm().setPassword(domainRegistry.getMessages().password());
        i18n.getForm().setSubmit(domainRegistry.getMessages().login());

        var loginForm = new LoginForm();
        loginForm.addLoginListener(event -> {
            try {
                var jwtToken = session.login(
                    event.getUsername(),
                    event.getPassword()
                );

                var cookie = new Cookie(SchooltripApplication.SECURITY_COOKIE_NAME, jwtToken);
                cookie.setMaxAge(5 * 60 * 60); // This is one hour less than the jwt token.
                cookie.setSecure(true);
                VaadinResponse.getCurrent().addCookie(cookie);

                UI.getCurrent().navigate(SchoolTripsView.class);
            } catch (Exception ex) {
                log.warn("Exception during login.", ex);
                loginForm.setEnabled(true);
                loginForm.setError(true);
            }
        });

        this.setSizeFull();
        this.setAlignItems(Alignment.CENTER);
        this.setJustifyContentMode(JustifyContentMode.CENTER);
        this.add(loginForm);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (userSession.getRegisteredUser().isPresent()) {
            event.forwardTo(SchoolTripsView.class);
        }
    }

}
