package com.wellnr.schooltrip.views;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.wellnr.schooltrip.core.application.commands.ConfirmStudentRegistrationCommand;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.views.components.Container;

@Route("/students/confirm-registration/:token")
@PageTitle("School Trip")
public class StudentConfirmRegistrationView extends Container implements BeforeEnterObserver {

    private final SchoolTripCommandRunner commandRunner;

    public StudentConfirmRegistrationView(SchoolTripCommandRunner commandRunner) {
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
