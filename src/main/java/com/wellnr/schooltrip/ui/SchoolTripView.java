package com.wellnr.schooltrip.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
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
import com.wellnr.ddd.DomainException;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.application.commands.*;
import com.wellnr.schooltrip.core.model.student.RegistrationState;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.questionaire.Ski;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.ui.components.CommandForm;
import com.wellnr.schooltrip.ui.components.CommandFormBuilder;
import com.wellnr.schooltrip.ui.components.ExcelImportDialog;
import com.wellnr.schooltrip.ui.views.trips.AbstractSchoolTripView;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Route(value = "trips/:name", layout = ApplicationAppLayout.class)
public class SchoolTripView extends AbstractSchoolTripView {

    private Grid<Student> students;

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

        var actions = new ActionsBar();

        this.students = new StudentsGrid();
        this.students.setItems(schoolTrip.students());

        this.studentDetails = new StudentDetails();

        var gridArea = new VerticalLayout();
        gridArea.setAlignItems(FlexComponent.Alignment.START);

        gridArea.add(actions);
        gridArea.add(students);

        gridArea.setAlignSelf(FlexComponent.Alignment.START, actions);
        gridArea.setFlexGrow(0, actions);
        gridArea.setFlexGrow(1, students);

        var mainArea = new HorizontalLayout(gridArea, studentDetails);
        mainArea.setAlignItems(FlexComponent.Alignment.STRETCH);
        mainArea.setFlexGrow(1, gridArea);
        mainArea.setFlexGrow(0, studentDetails);
        mainArea.setWidthFull();

        mainArea.setMargin(false);
        mainArea.setPadding(true);
        mainArea.setSizeFull();

        this.removeAll();
        this.add(mainArea);
    }

    private void importStudents(List<RegisterStudentCommand> commands) {
        /*
         * Create classes.
         */
        commands
            .stream()
            .map(RegisterStudentCommand::getSchoolClass)
            .collect(Collectors.toSet())
            .stream()
            .map(schoolClass -> RegisterSchoolClassCommand.apply(
                schoolTrip.schoolTrip().getName(),
                schoolClass
            ))
            .forEach(cmd -> {
                try {
                    var result = commandRunner.run(cmd);
                    System.out.println(result.getMessage());
                } catch (DomainException ex) {
                    System.out.println(ex.getSummary());
                }
            });

        /*
         * Register students.
         */
        commands.forEach(cmd -> {
            try {
                var result = commandRunner.run(cmd);
                System.out.println(result.getMessage());
            } catch (DomainException ex) {
                System.out.println(ex.getSummary());
            }
        });

        this.reload();
    }

    private class ActionsBar extends HorizontalLayout {

        public ActionsBar() {
            var actionsMenuBar = new MenuBar();
            actionsMenuBar.addThemeVariants(MenuBarVariant.LUMO_ICON, MenuBarVariant.LUMO_PRIMARY);
            var actions = actionsMenuBar.addItem(VaadinIcon.TASKS.create());
            var actionsSubMenu = actions.getSubMenu();

            actionsSubMenu.addItem("Add Student");
            var mnuUpload = actionsSubMenu.addItem("Upload Students");
            actionsSubMenu.add(new Hr());
            actionsSubMenu.addItem("Assign Sequential Identifier");
            actionsSubMenu.addItem("Remove not registered Students");
            actionsSubMenu.add(new Hr());
            actionsSubMenu.addItem("Export Excel");

            mnuUpload.addClickListener(event -> openImportFromExcelDialog());

            setWidthFull();
            setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            setAlignItems(FlexComponent.Alignment.CENTER);
            add(actionsMenuBar);
        }

        private void openImportFromExcelDialog() {
            var dialog = new ExcelImportDialog<>(
                List.of("Klasse", "Vorname", "Nachname"),
                parameters -> {
                    var schoolClass = parameters.get(0).toString();
                    var firstName = parameters.get(1).toString();
                    var lastName = parameters.get(2).toString();

                    return RegisterStudentCommand.apply(
                        schoolTrip.schoolTrip().getName(), schoolClass, firstName, lastName
                    );
                }
            );

            dialog.addDataImportedListener(imported -> importStudents(imported.getResult()));

            this.add(dialog);
            dialog.open();
        }

    }

    private class StudentDetails extends Scroller {

        private final CommandForm<MessageResult<Student>, UpdateStudentPropertiesCommand> basics;

        private final CommandForm<MessageResult<Nothing>, AddPaymentCommand> addPaymentForm;

        private final StudentRegistration registration;

        public StudentDetails() {
            setWidth("800px");
            setMaxWidth("800");
            setVisible(false);

            this.basics = new CommandFormBuilder<>(
                UpdateStudentPropertiesCommand.class,
                commandRunner
            )
                .addVariant("schoolClass", CommandFormBuilder.FormVariant.LINE_BREAK_AFTER)
                .build();

            this.addPaymentForm = new CommandFormBuilder<>(
                AddPaymentCommand.class,
                commandRunner
            )
                .addVariant(
                    "type",
                    CommandFormBuilder.FormVariant.LINE_BREAK_AFTER,
                    CommandFormBuilder.FormVariant.LINE_BREAK_AFTER
                )
                .addVariant(
                    "description",
                    CommandFormBuilder.FormVariant.FULL_WIDTH,
                    CommandFormBuilder.FormVariant.LINE_BREAK_AFTER
                )
                .addVariant(
                    "amount",
                    CommandFormBuilder.FormVariant.EURO_SUFFIX
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

    private class StudentsGrid extends Grid<Student> {

        public StudentsGrid() {
            var classColumn = this
                .addColumn(Student::getSchoolClass)
                .setHeader("Class")
                .setSortable(true)
                .setFrozen(true)
                .setAutoWidth(true);

            var lastNameColumn = this
                .addColumn(Student::getLastName)
                .setHeader("Last Name")
                .setSortable(true)
                .setFrozen(true)
                .setAutoWidth(true);

            var firstNameColumn = this
                .addColumn(Student::getFirstName)
                .setHeader("First Name")
                .setSortable(true)
                .setFrozen(true)
                .setAutoWidth(true);

            this
                .addComponentColumn(student -> {
                    var span = new Span();
                    span.setClassName("app--trip--registration-state--created");
                    span.add(VaadinIcon.CHECK.create());
                    return span;
                }).setHeader("Status");

            this
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
                .addColumn(student -> student
                    .getQuestionaire()
                    .map(q -> q.getDisziplin().getRental().map(i -> "Yes").orElse("No"))
                    .orElse("")
                )
                .setHeader("Ski/ Snowboard");

            this
                .addColumn(student -> student
                    .getQuestionaire()
                    .map(q -> q.getDisziplin().getBootRental().map(i -> "Yes").orElse("No"))
                    .orElse("")
                )
                .setHeader("Schuhe");

            this
                .addComponentColumn(student -> {
                    var bttEdit = new Button("Edit");
                    bttEdit.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
                    bttEdit.addClickListener(event -> {
                        studentDetails.open(student);
                        getSelectionModel().select(student);
                    });

                    var layout = new HorizontalLayout(bttEdit);
                    layout.setAlignItems(FlexComponent.Alignment.CENTER);
                    layout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
                    return layout;
                })
                .setHeader("")
                .setTextAlign(ColumnTextAlign.END)
                .setFrozenToEnd(true);
            this.setMultiSort(true);

            this.sort(List.of(
                new GridSortOrder<>(classColumn, SortDirection.ASCENDING),
                new GridSortOrder<>(lastNameColumn, SortDirection.ASCENDING),
                new GridSortOrder<>(firstNameColumn, SortDirection.ASCENDING)
            ));

            this.addSelectionListener(event -> {
                if (event.getFirstSelectedItem().isPresent()) {
                    studentDetails.open(event.getFirstSelectedItem().get());
                } else {
                    studentDetails.close();
                }
            });
        }

    }

}
