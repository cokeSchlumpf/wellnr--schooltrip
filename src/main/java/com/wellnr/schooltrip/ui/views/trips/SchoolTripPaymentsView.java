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
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.grid.ApplicationAmountLabelBuilder;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridWithControls;
import com.wellnr.schooltrip.ui.components.student.StudentsGrid;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

@Route(value = "trips/:name/payments", layout = ApplicationAppLayout.class)
public class SchoolTripPaymentsView extends AbstractSchoolTripGridView {

    private final SchoolTripMessages i18n;

    public SchoolTripPaymentsView(
            ApplicationCommandRunner commandRunner, ApplicationUserSession userSession
    ) {
        super(commandRunner, userSession);
        this.i18n = userSession.getMessages();
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

    @Override
    protected ApplicationGridWithControls<Student> createStudentsGrid() {
        return new StudentsPaymentGrid();
    }

    private class StudentsPaymentGrid extends StudentsGrid {

        public StudentsPaymentGrid() {
            super(i18n);
            this.addDefaultColumnsWithSorting(schoolTrip.schoolTrip());

            this
                    .addComponentColumnForRegisteredStudent(student -> student
                            .getPriceLineItems(Either.fromRight(schoolTrip.schoolTrip()), i18n)
                            .map(items -> new Span(items.getSumFormatted(i18n.currencyNumberFormat())))
                            .orElse(new Span("-"))
                    )
                    .setTextAlign(ColumnTextAlign.END)
                    .setHeader(i18n.expectedAmount())
                    .setComparator(student -> student.getPriceLineItems(Either.fromRight(schoolTrip.schoolTrip()), i18n).map(AbstractLineItems::getSum).orElse(0d))
                    .setSortable(true);

            this
                    .addComponentColumnForRegisteredStudent(
                            student -> new Span(student.getPayments().getSumFormatted(i18n.currencyNumberFormat()))
                    )
                    .setTextAlign(ColumnTextAlign.END)
                    .setHeader(i18n.paidAmount())
                    .setComparator(student -> student.getPayments().getSum())
                    .setSortable(true);

            this
                    .addComponentColumnForRegisteredStudent(student -> {
                        var diff = student.getOpenPaymentAmount(Either.fromRight(schoolTrip.schoolTrip()), i18n);

                        return ApplicationAmountLabelBuilder
                                .apply(diff, "€")
                                .withInverted(true)
                                .build();
                    })
                    .setTextAlign(ColumnTextAlign.END)
                    .setHeader(i18n.diffAmount())
                    .setComparator(student -> student.getOpenPaymentAmount(Either.fromRight(schoolTrip.schoolTrip()), i18n))
                    .setSortable(true);

            var bttNew = this.getMenuBar().addItem(i18n.enterPayments());
            bttNew.addClickListener(ignore -> UI.getCurrent().navigate(
                    SchoolTripAddStudentView.class, SchoolTripAddStudentView.getRouteParameters(
                            schoolTrip.schoolTrip().getName()
                    )
            ));
        }

    }

}
