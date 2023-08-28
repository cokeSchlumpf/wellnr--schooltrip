package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.questionaire.Ski;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.infrastructure.UserSession;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridWithControls;
import com.wellnr.schooltrip.ui.components.student.StudentsGrid;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

@Route(value = "trips/:name/disciplines", layout = ApplicationAppLayout.class)
public class SchoolTripDisciplineView extends AbstractSchoolTripGridView {

    public SchoolTripDisciplineView(
        SchoolTripCommandRunner commandRunner, UserSession userSession
    ) {
        super(commandRunner, userSession);
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
            this.addDefaultColumnsWithSorting(schoolTrip.schoolTrip());

            this
                .addComponentColumnForRegisteredStudent(
                    (student, questionnaire) -> new Span(questionnaire.getDisziplin().getExperience().getValue())
                )
                .setHeader("Experience");

            this
                .addComponentColumnForRegisteredStudent((student, questionnaire) -> {
                    if (questionnaire.getDisziplin() instanceof Ski) {
                        return new Span("Ski");
                    } else {
                        return new Span("Snowboard");
                    }
                })
                .setHeader("Disziplin");

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
                .setHeader("Ausleihe Ski/ Board");

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
                .setHeader("Ausleihe Schuhe");

            this
                .addComponentColumnForRegisteredStudent((student, questionnaire) -> {
                    // TODO: Add field as soon as present.
                    return new Span("-");
                })
                .setHeader("Ausleihe Helm");
        }

    }

}
