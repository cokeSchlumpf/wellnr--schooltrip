package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.questionaire.Ski;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridWithControls;
import com.wellnr.schooltrip.ui.components.student.StudentsGrid;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

@Route(value = "trips/:name/disciplines", layout = ApplicationAppLayout.class)
public class SchoolTripDisciplineView extends AbstractSchoolTripGridView {

    private final SchoolTripMessages i18n;

    public SchoolTripDisciplineView(
        ApplicationCommandRunner commandRunner, ApplicationUserSession userSession
    ) {
        super(commandRunner, userSession);
        this.i18n = userSession.getMessages();
    }

    @Override
    protected ApplicationGridWithControls<Student> createStudentsGrid() {
        return new StudentsPaymentGrid();
    }

    /**
     * Helper method to generate the route parameters for this view.
     *
     * @param name The name of the school trip to route to.
     * @return The set of route parameters required to call this view.
     */
    public static RouteParameters getRouteParameters(String name) {
        return new RouteParameters(
            new RouteParam("name", name)
        );
    }

    private class StudentsPaymentGrid extends StudentsGrid {

        public StudentsPaymentGrid() {
            super(i18n);
            this.addDefaultColumnsWithSorting(schoolTrip.schoolTrip());

            this
                .addComponentColumnForRegisteredStudent(
                    (student, questionnaire) -> new Span(questionnaire.getDisziplin().getExperience().getValue())
                )
                .setHeader(i18n.experience());

            this
                .addComponentColumnForRegisteredStudent((student, questionnaire) -> {
                    if (questionnaire.getDisziplin() instanceof Ski) {
                        return new Span(i18n.ski());
                    } else {
                        return new Span(i18n.snowboardRentalPrice());
                    }
                })
                .setHeader(i18n.discipline());

            this
                .addComponentColumnForRegisteredStudent((student, questionnaire) -> {
                    if (questionnaire.getDisziplin().getRental().isPresent()) {
                        var rental = questionnaire.getDisziplin().getRental().get();
                        return new Span(rental.getHeight() + "cm / " + rental.getWeight() + "kg");
                    } else {
                        return new Span("-");
                    }
                })
                .setTextAlign(ColumnTextAlign.CENTER)
                .setHeader(i18n.rental());

            this
                .addComponentColumnForRegisteredStudent((student, questionnaire) -> {
                    if (questionnaire.getDisziplin().getBootRental().isPresent()) {
                        return new Span(String.valueOf(
                            questionnaire.getDisziplin().getBootRental().get().getSize()
                        ));
                    } else {
                        return new Span("-");
                    }
                })
                .setTextAlign(ColumnTextAlign.CENTER)
                .setHeader(i18n.bootRental());

            this
                .addComponentColumnForRegisteredStudent((student, questionnaire) -> {
                    // TODO: Add field as soon as present.
                    return new Span("-");
                })
                .setHeader(i18n.helmetRental());
        }

    }

}
