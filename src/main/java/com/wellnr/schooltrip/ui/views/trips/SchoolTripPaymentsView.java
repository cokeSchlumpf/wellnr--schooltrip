package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.wellnr.common.markup.Either;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.payments.AbstractLineItems;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.ui.components.grid.ApplicationAmountLabelBuilder;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridWithControls;
import com.wellnr.schooltrip.ui.components.student.StudentsGrid;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

@Route(value = "trips/:name/payments", layout = ApplicationAppLayout.class)
public class SchoolTripPaymentsView extends AbstractSchoolTripGridView {

    public SchoolTripPaymentsView(SchoolTripCommandRunner commandRunner) {
        super(commandRunner);
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
            this.addDefaultColumnsWithSorting();

            this
                .addComponentColumnForRegisteredStudent(student -> student
                    .getPriceLineItems(Either.fromRight(schoolTrip.schoolTrip()))
                    .map(items -> new Span(items.getSumFormatted()))
                    .orElse(new Span("-"))
                )
                .setTextAlign(ColumnTextAlign.END)
                .setHeader("Expected");

            this
                .addComponentColumnForRegisteredStudent(
                    student -> new Span(student.getPayments().getSumFormatted())
                )
                .setTextAlign(ColumnTextAlign.END)
                .setHeader("Paid");

            this
                .addComponentColumnForRegisteredStudent(student -> {
                    var expected = student
                        .getPriceLineItems(Either.fromRight(schoolTrip.schoolTrip()))
                        .map(AbstractLineItems::getSum)
                        .orElse(0d);

                    var paid = student
                        .getPayments()
                        .getSum();

                    var diff = expected - paid;

                    return ApplicationAmountLabelBuilder
                        .apply(diff, "€")
                        .withInverted(true)
                        .build();
                })
                .setTextAlign(ColumnTextAlign.END)
                .setHeader("Diff");

            var bttNew = this.getMenuBar().addItem("Enter payments");
            bttNew.addClickListener(ignore -> UI.getCurrent().navigate(
                SchoolTripAddStudentView.class, SchoolTripAddStudentView.getRouteParameters(
                    schoolTrip.schoolTrip().getName()
                )
            ));
        }

    }

}
