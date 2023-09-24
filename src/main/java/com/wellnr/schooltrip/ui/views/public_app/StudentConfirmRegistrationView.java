package com.wellnr.schooltrip.ui.views.public_app;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.wellnr.schooltrip.core.application.commands.schooltrip.ConfirmStudentRegistrationCommand;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;

@Route("/students/confirm-registration/:token")
public class StudentConfirmRegistrationView extends AbstractPublicAppView implements BeforeEnterObserver {

    private final ApplicationCommandRunner commandRunner;

    public StudentConfirmRegistrationView(ApplicationUserSession userSession, ApplicationCommandRunner commandRunner) {
        super(userSession);
        this.commandRunner = commandRunner;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        var token = beforeEnterEvent
            .getRouteParameters()
            .get("token")
            .orElseThrow();

        var student = commandRunner
            .run(
                ConfirmStudentRegistrationCommand.apply(token)
            )
            .getData();

        beforeEnterEvent.forwardTo(
            StudentRegisteredView.class,
            StudentRegisteredView.getRouteParameters(student)
        );
    }

}
