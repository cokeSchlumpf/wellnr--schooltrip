package com.wellnr.schooltrip.ui.components;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.textfield.TextField;
import com.wellnr.common.functions.Procedure1;
import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.application.commands.schooltrip.AddPaymentCommand;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.questionaire.TShirtSelection;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandForm;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandFormBuilder;

import java.util.List;

public class EnterPaymentsDialog extends Dialog {

    private final SchoolTripMessages i18n;

    private final ApplicationCommandRunner commandRunner;

    private final List<Student> students;

    private final ApplicationCommandForm<MessageResult<Nothing>, AddPaymentCommand> addPaymentForm;

    private AddPaymentCommand command;

    public EnterPaymentsDialog(SchoolTripMessages i18n, ApplicationCommandRunner commandRunner, List<Student> students) {
        this.i18n = i18n;
        this.commandRunner = commandRunner;
        this.students = students;

        this.setHeaderTitle(i18n.enterPayments());
        this.setMinWidth("60%");
        this.setMinHeight("400px");

        this.addPaymentForm = getAddPaymentForm();
        var studentSearchControls = getStudentSearchControls();
        this.add(studentSearchControls, new Hr(), this.addPaymentForm);
    }

    private ApplicationCommandForm<MessageResult<Nothing>, AddPaymentCommand> getAddPaymentForm() {
        var form = new ApplicationCommandFormBuilder<>(
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
                .setI18nMessages(i18n)
                .build();

        form.addCompletionListener(event -> {
            this.command = event.getCommand();
        });

        return form;
    }

    private FormLayout getStudentSearchControls() {
        var formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 5));

        var txtId = new ComboBox<Student>(i18n.id());
        txtId.setItems(students);
        txtId.setItemLabelGenerator(s -> s.getSchoolTripStudentId().map(Object::toString).orElse("-"));

        var txtSchoolClass = new ComboBox<Student>(i18n.schoolClass());
        txtSchoolClass.setItems(students);
        txtSchoolClass.setItemLabelGenerator(Student::getSchoolClass);

        var txtFirstName = new ComboBox<Student>(i18n.firstName());
        txtFirstName.setItems(students);
        txtFirstName.setItemLabelGenerator(Student::getFirstName);

        var txtLastName = new ComboBox<Student>(i18n.lastName());
        txtLastName.setItems(students);
        txtLastName.setItemLabelGenerator(Student::getLastName);

        var txtTShirt = new TextField(i18n.tripTShirt());
        txtTShirt.setEnabled(false);

        formLayout.add(txtId, txtSchoolClass, txtFirstName, txtLastName, txtTShirt);

        Procedure1<Student> valueChangeListener = student -> {
            txtId.setValue(student);
            txtSchoolClass.setValue(student);
            txtFirstName.setValue(student);
            txtLastName.setValue(student);
            txtTShirt.setValue(i18n.tShirtSelection(
                student.getQuestionnaire().map(q -> q.getTShirtSelection()).orElse(TShirtSelection.NONE),
                i18n
            ));

            if (this.command == null) {
                this.addPaymentForm.setGetInitialCommand(() -> AddPaymentCommand.apply(student));
            } else {
                this.addPaymentForm.setGetInitialCommand(() -> this.command.withStudentId(student));
            }
        };

        txtId.addValueChangeListener(e -> valueChangeListener.run(e.getValue()));
        txtSchoolClass.addValueChangeListener(e -> valueChangeListener.run(e.getValue()));
        txtFirstName.addValueChangeListener(e -> valueChangeListener.run(e.getValue()));
        txtLastName.addValueChangeListener(e -> valueChangeListener.run(e.getValue()));

        return formLayout;
    }

}
