package com.wellnr.schooltrip.ui;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.wellnr.schooltrip.core.application.commands.schooltrip.ConfirmStudentRegistrationCommand;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.ui.components.ApplicationContentContainer;

@Route("/students/confirm-registration/:token")
@PageTitle("School Trip")
public class StudentConfirmRegistrationView extends ApplicationContentContainer implements BeforeEnterObserver {

    private final ApplicationCommandRunner commandRunner;

    public StudentConfirmRegistrationView(ApplicationCommandRunner commandRunner) {
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

        /*
         * Initialize view.
         */
        this.add(new Paragraph(
            "Klasse! Registrerung ist abgechlossen. Dann kann die Gaudi losgehen! Wir freuen uns auf `" + student.getDisplayName() + "`"
        ));
    }

}
