package com.wellnr.schooltrip.ui.public_app;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.*;
import com.wellnr.schooltrip.core.application.commands.schooltrip.RegisteredStudentViewCommand;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.public_app.HeadlineWithTitle;
import com.wellnr.schooltrip.ui.layout.AbstractPublicAppView;

@Route("/students/registered/:token")
public class StudentRegisteredView extends AbstractPublicAppView implements BeforeEnterObserver {

    private final ApplicationCommandRunner commandRunner;

    public StudentRegisteredView(
        ApplicationUserSession userSession, ApplicationCommandRunner commandRunner
    ) {
        super(userSession);
        this.commandRunner = commandRunner;
    }

    public static RouteParameters getRouteParameters(Student student) {
        return new RouteParameters(
            new RouteParam("token", student.getConfirmationToken())
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        var token = beforeEnterEvent
            .getRouteParameters()
            .get("token")
            .orElseThrow();

        var projection = commandRunner
            .run(RegisteredStudentViewCommand.apply(token))
            .getData();

        /*
         * Initialize view.
         */
        var headline = new HeadlineWithTitle(
            projection.schoolTrip().getTitle(),
            i18n.studentRegisteredViewHeadline(projection.student())
        );

        var introduction = new Paragraph();
        introduction.setText(i18n.studentRegisteredViewInfo(projection.student()));

        var gotoUpdate = new Button(i18n.updateRegistration());
        gotoUpdate.addClickListener(event -> {
            UI.getCurrent().navigate(
                StudentUpdateView.class,
                StudentUpdateView.getRouteParameters(projection.student())
            );
        });

        this.contentContainer.add(
            headline,
            introduction,
            gotoUpdate
        );
    }

}
