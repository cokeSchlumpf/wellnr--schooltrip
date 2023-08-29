package com.wellnr.schooltrip.ui.components.student;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.application.commands.schooltrip.AddPaymentCommand;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.payments.Payment;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandForm;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandFormBuilder;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class StudentPaymentAdminControl extends VerticalLayout {

    private final ApplicationCommandForm<MessageResult<Nothing>, AddPaymentCommand> addPaymentForm;

    private final Grid<Payment> payments;

    public StudentPaymentAdminControl(ApplicationCommandRunner commandRunner) {
        this.setPadding(false);
        this.setMargin(false);

        var format = new DecimalFormat("#.00");

        this.payments = new Grid<>(Payment.class, false);
        this.payments
            .addComponentColumn(payment -> new Text(payment.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .setHeader("Date");
        this.payments
            .addComponentColumn(payment -> new Text(payment.getDescription()))
            .setHeader("Description");
        this.payments
            .addComponentColumn(payment -> new Text(format.format(payment.getAmount())))
            .setHeader("Amount")
            .setTextAlign(ColumnTextAlign.END);

        this.payments.setAllRowsVisible(true);


        this.addPaymentForm = new ApplicationCommandFormBuilder<>(
            AddPaymentCommand.class,
            commandRunner
        )
            .addVariant(
                "type",
                ApplicationCommandFormBuilder.FormVariant.LINE_BREAK_AFTER,
                ApplicationCommandFormBuilder.FormVariant.LINE_BREAK_AFTER
            )
            .addVariant(
                "description",
                ApplicationCommandFormBuilder.FormVariant.FULL_WIDTH,
                ApplicationCommandFormBuilder.FormVariant.LINE_BREAK_AFTER
            )
            .addVariant(
                "amount",
                ApplicationCommandFormBuilder.FormVariant.EURO_SUFFIX
            )
            .build();

        this.addPaymentForm.addCompletionListener(
            event -> fireEvent(new StudentPaymentsUpdatedEvent(this, true))
        );
    }

    public Registration addPaymentUpdatedListener(
        ComponentEventListener<StudentPaymentsUpdatedEvent> listener) {

        return addListener(StudentPaymentsUpdatedEvent.class, listener);
    }

    public void setStudent(Student student) {
        this.addPaymentForm.setGetInitialCommand(
            () -> AddPaymentCommand.apply(student)
        );

        this.payments.setItems(student.getPayments().getItems());

        this.removeAll();
        if (!student.getPayments().getItems().isEmpty()) {
            this.add(new H4("Payments made"));
            this.add(this.payments);
        }

        this.add(new H4("Record payment"));
        this.add(this.addPaymentForm);
    }

}
