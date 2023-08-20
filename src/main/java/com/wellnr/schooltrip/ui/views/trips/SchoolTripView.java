package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.wellnr.common.markup.Nothing;
import com.wellnr.common.markup.Tuple2;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.application.commands.*;
import com.wellnr.schooltrip.core.model.student.RegistrationState;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.questionaire.Ski;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandForm;
import com.wellnr.schooltrip.ui.components.forms.ApplicationCommandFormBuilder;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridWithControls;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

import java.util.List;
import java.util.Objects;

@Route(value = "trips/:name", layout = ApplicationAppLayout.class)
public class SchoolTripView extends AbstractSchoolTripView {

    private ApplicationGridWithControls<Student> students;

    private StudentDetails studentDetails;

    public SchoolTripView(SchoolTripCommandRunner commandRunner) {
        super(commandRunner);
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
    protected void updateView() {
        if (Objects.isNull(schoolTrip)) {
            return;
        }

        if (Objects.isNull(this.students)) {
            this.students = new StudentsGrid();
        }

        this.students.getGrid().setItems(schoolTrip.students());
        this.studentDetails = new StudentDetails();

        var mainArea = new HorizontalLayout(students, studentDetails);
        mainArea.setAlignItems(FlexComponent.Alignment.STRETCH);
        mainArea.setFlexGrow(1, students);
        mainArea.setFlexGrow(0, studentDetails);
        mainArea.setWidthFull();

        mainArea.setMargin(false);
        mainArea.setPadding(false);
        mainArea.setSizeFull();

        this.removeAll();
        this.add(mainArea);
    }

    private class StudentDetails extends Scroller {

        private final ApplicationCommandForm<MessageResult<Student>, UpdateStudentPropertiesCommand> basics;

        private final ApplicationCommandForm<MessageResult<Nothing>, AddPaymentCommand> addPaymentForm;

        private final StudentRegistration registration;

        private Student student;

        public StudentDetails() {
            setWidth("800px");
            setMaxWidth("800");
            setVisible(false);

            this.basics = new ApplicationCommandFormBuilder<>(
                UpdateStudentPropertiesCommand.class,
                commandRunner
            )
                .addVariant("schoolClass", ApplicationCommandFormBuilder.FormVariant.LINE_BREAK_AFTER)
                .setFieldPossibleValues(
                    "schoolClass",
                    schoolTrip
                        .schoolTrip()
                        .getSchoolClasses()
                        .stream()
                        .map(cl -> Tuple2.apply(cl.getName(), cl.getName()))
                        .toList()
                )
                .build();

            this.basics.addCompletionListener(event -> {
                reload();

                var selectedStudent = schoolTrip
                    .students()
                    .stream()
                    .filter(s -> event.getResult().getData().getId().equals(s.getId()))
                    .findFirst();

                selectedStudent.ifPresent(s -> students.getGrid().select(s));
            });

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

            this.registration = new StudentRegistration();

            var tabs = new TabSheet();

            tabs.add(
                new Tab("Student"),
                basics
            );

            tabs.add(
                new Tab("Registration"),
                this.registration
            );

            tabs.add(
                new Tab("Payments"),
                this.addPaymentForm
            );

            var vl = new VerticalLayout(tabs);
            this.setContent(vl);
        }

        public void open(Student student) {
            this.setVisible(true);
            this.basics.setGetInitialCommand(
                () -> UpdateStudentPropertiesCommand.apply(student)
            );
            this.addPaymentForm.setGetInitialCommand(
                () -> AddPaymentCommand.apply(student)
            );
            this.registration.setStudent(student);
        }

        public void close() {
            this.setVisible(false);
        }
    }

    private class StudentRegistration extends VerticalLayout {

        private final Paragraph infoText;

        private final Button openRegistration;

        private final Button confirmRegistration;

        private Student student;

        public StudentRegistration() {
            this.infoText = new Paragraph();
            this.openRegistration = new Button("Open registration");
            this.openRegistration.addClickListener(event -> {

            });

            this.confirmRegistration = new Button("Confirm Registration");
            this.confirmRegistration.addClickListener(e -> {
                var cmd = ConfirmStudentRegistrationCommand.apply(student.getConfirmationToken());
                var student = commandRunner.run(cmd).getData();

                studentDetails.open(student);
            });
        }

        public void setStudent(Student student) {
            this.removeAll();

            if (student.getRegistrationState().equals(RegistrationState.CREATED)) {
                this.infoText.setText("Student is not registered yet. You may register the student manually.");
                this.add(infoText, openRegistration);
            } else if (student.getRegistrationState().equals(RegistrationState.WAITING_FOR_CONFIRMATION)) {
                this.infoText.setText("Student has been registered, but confirmation link has not been called.");
                this.add(infoText, confirmRegistration);
            } else if (student.getRegistrationState().equals(RegistrationState.REGISTERED)) {
                this.infoText.setText("Student has been registred. You may change the configurations of the student.");
                this.add(infoText);

                // TODO
            }
        }

    }

    private class StudentsGrid extends ApplicationGridWithControls<Student> {

        public StudentsGrid() {
            var classColumn = this
                .getGrid()
                .addColumn(Student::getSchoolClass)
                .setHeader("Class")
                .setSortable(true)
                .setFrozen(true)
                .setAutoWidth(true);

            var lastNameColumn = this
                .getGrid()
                .addColumn(Student::getLastName)
                .setHeader("Last Name")
                .setSortable(true)
                .setFrozen(true)
                .setAutoWidth(true);

            var firstNameColumn = this
                .getGrid()
                .addColumn(Student::getFirstName)
                .setHeader("First Name")
                .setSortable(true)
                .setFrozen(true)
                .setAutoWidth(true);

            this
                .getGrid()
                .addComponentColumn(student -> {
                    var span = new Span();
                    span.setClassName("app--trip--registration-state--created");
                    span.add(VaadinIcon.CHECK.create());
                    return span;
                }).setHeader("Status");

            this
                .getGrid()
                .addColumn(student -> student
                    .getQuestionaire()
                    .map(q -> {
                        if (q.getDisziplin() instanceof Ski) {
                            return "Ski";
                        } else {
                            return "Snowboard";
                        }
                    })
                    .orElse("")
                )
                .setHeader("Disziplin");

            this
                .getGrid()
                .addActionsColumn(student -> {
                    var bttEdit = new Button("Edit");
                    bttEdit.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
                    bttEdit.addClickListener(event -> {
                        studentDetails.open(student);
                        this.getGrid().getSelectionModel().select(student);
                    });

                    return List.of(bttEdit);
                });

            this.getGrid().setMultiSort(true);

            this.getGrid().sort(List.of(
                new GridSortOrder<>(classColumn, SortDirection.ASCENDING),
                new GridSortOrder<>(lastNameColumn, SortDirection.ASCENDING),
                new GridSortOrder<>(firstNameColumn, SortDirection.ASCENDING)
            ));

            this.getGrid().addSelectionListener(event -> {
                if (event.getFirstSelectedItem().isPresent()) {
                    studentDetails.open(event.getFirstSelectedItem().get());
                } else {
                    studentDetails.close();
                }
            });

            var bttNew = this.getMenuBar().addItem("Add Student");
            bttNew.addClickListener(ignore -> UI.getCurrent().navigate(
                SchoolTripAddStudentView.class, SchoolTripAddStudentView.getRouteParameters(
                    schoolTrip.schoolTrip().getName()
                )
            ));
        }

    }

}
