package com.wellnr.schooltrip.ui.views.public_app;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.*;
import com.wellnr.common.markup.Either;
import com.wellnr.schooltrip.core.application.commands.schooltrip.RegisteredStudentViewCommand;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.payments.PriceLineItems;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.Markdown;
import com.wellnr.schooltrip.ui.components.public_app.HeadlineWithTitle;

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

        this.schoolTrip = projection.schoolTrip();

        /*
         * Initialize view.
         */
        var headline = new HeadlineWithTitle(
            projection.schoolTrip().getTitle(),
            i18n.studentRegisteredViewHeadline(projection.student())
        );

        var introductionInfo = new Paragraph();
        introductionInfo.setText(i18n.studentRegisteredViewInfo(projection.student()));

        var gotoUpdate = new Button(i18n.updateRegistration());
        gotoUpdate.addClickListener(event -> {
            UI.getCurrent().navigate(
                StudentUpdateView.class,
                StudentUpdateView.getRouteParameters(projection.student())
            );
        });

        var paymentsHeadline = new H3(i18n.payments());
        double initialPaymentAmount = Student.INITIAL_PAYMENT_AMOUNT;
        double expectedPaymentAmount = projection
            .student()
            .getPriceLineItems(Either.fromRight(schoolTrip), i18n)
            .map(PriceLineItems::getAmountPaymentsBeforeTrip)
            .orElse(0d);

        var alreadyPaidAmount = projection.student().getPayments().getSum();
        var rentalFees = projection
            .student()
            .getPriceLineItems(Either.fromRight(schoolTrip), i18n)
            .map(PriceLineItems::getCashFees)
            .orElse(0d);

        var paymentInfo = i18n
            .paymentsInfo(
                projection.student(), expectedPaymentAmount, alreadyPaidAmount,
                rentalFees, initialPaymentAmount, expectedPaymentAmount - initialPaymentAmount, i18n
            )
            .stream()
            .map(p -> (Component) Markdown.apply(p))
            .toList();

        var introduction = new Div();
        introduction.addClassName("app__student-registered-view__introduction");
        introduction.add(headline, introductionInfo, gotoUpdate);

        var container = new Div();
        container.addClassName("app__student-registered-view__section");
        container.add(paymentsHeadline);
        container.add(paymentInfo);

        this.contentContainer.add(
            introduction,
            container
        );

        this.contentContainer.add(paymentInfo);
    }

}
