package com.wellnr.schooltrip.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;
import com.wellnr.common.Operators;
import com.wellnr.common.markup.When;
import com.wellnr.ddd.DomainException;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.application.commands.ConfirmStudentRegistrationCommand;
import com.wellnr.schooltrip.core.application.commands.RegisterSchoolClassCommand;
import com.wellnr.schooltrip.core.application.commands.RegisterStudentCommand;
import com.wellnr.schooltrip.core.application.commands.UpdateStudentPropertiesCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolClass;
import com.wellnr.schooltrip.core.model.student.Gender;
import com.wellnr.schooltrip.core.model.student.RegistrationState;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.questionaire.Ski;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.views.components.CommandForm;
import com.wellnr.schooltrip.views.components.CommandFormBuilder;
import com.wellnr.schooltrip.views.components.ExcelImportDialog;
import com.wellnr.schooltrip.views.layout.AbstractSchoolTripView;
import com.wellnr.schooltrip.views.layout.SchoolTripAppLayout;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Route(value = "trips/:name", layout = SchoolTripAppLayout.class)
public class SchoolTripView extends AbstractSchoolTripView {

    private Grid<Student> students;

    private StudentDetails studentDetails;

    public SchoolTripView(SchoolTripCommandRunner commandRunner) {
        super(true, commandRunner);
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
        mainArea.setFlexGrow(3, gridArea);
        mainArea.setFlexGrow(1, studentDetails);
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

    private class StudentDetails extends VerticalLayout {

        private CommandForm<MessageResult<Student>, UpdateStudentPropertiesCommand> basics;

        private final StudentRegistration registration;

        public StudentDetails() {
            setMaxWidth("640px");
            setVisible(false);

            var tabs = new Tabs();
            tabs.add(new Tab("Student"));
            tabs.add(new Tab("Registration"));
            tabs.add(new Tab("Payments"));

            this.basics = new CommandFormBuilder<>(
                UpdateStudentPropertiesCommand.class,
                commandRunner
            )
                .addVariant("schoolClass", CommandFormBuilder.FormVariant.LINE_BREAK_AFTER)
                .build();

            this.registration = new StudentRegistration();

            this.add(tabs);
            this.add(basics);
            this.add(new Hr());
            this.add(registration);
            this.add(new Hr());
        }

        public void open(Student student) {
            this.setVisible(true);
            this.basics.setGetInitialCommand(
                () -> UpdateStudentPropertiesCommand.apply(student)
            );
            this.registration.setStudent(student);
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

    private class StudentBasicsForm extends VerticalLayout {

        private final Select<String> schoolClass;

        private final TextField firstName;

        private final TextField lastName;

        private final DatePicker birthday;

        private final Select<Gender> gender;

        private final BeanValidationBinder<UpdateStudentPropertiesCommand> binder;

        private final Button save;

        private Student student;

        public StudentBasicsForm() {
            this.schoolClass = new Select<>();
            this.schoolClass.setLabel("Class");
            this.schoolClass.setItems(
                schoolTrip.schoolTrip().getSchoolClasses().stream().map(SchoolClass::getName).toList()
            );

            this.firstName = new TextField("Firstname");
            this.lastName = new TextField("Last Name");

            this.birthday = new DatePicker("Birthday");

            this.gender = new Select<>();
            this.gender.setItems(Gender.Male, Gender.Female);
            this.gender.setItemLabelGenerator(
                g -> When.isTrue(g.equals(Gender.Male)).then("male").otherwise("female")
            );

            this.binder = new BeanValidationBinder<>(UpdateStudentPropertiesCommand.class);
            this.binder.bindInstanceFields(this);

            this.save = new Button("Save");
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            save.addClickListener(event -> {
                var cmd = UpdateStudentPropertiesCommand.apply(student);
                Operators.suppressExceptions(() -> binder.writeBean(cmd));

                System.out.println(commandRunner.run(cmd).getData());
            });
            binder.addStatusChangeListener(
                status -> save.setEnabled(!status.hasValidationErrors())
            );

            var form = new FormLayout();
            form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
            form.add(schoolClass);
            form.add(new Div());
            form.add(firstName);
            form.add(lastName);
            form.add(birthday);
            form.add(gender);

            this.add(form);
            this.add(save);
        }

        public void setStudent(Student student) {
            var cmd = UpdateStudentPropertiesCommand.apply(student);

            this.student = student;
            this.binder.readBean(cmd);
            this.save.setEnabled(true);
        }

    }

    private class StudentsGrid extends Grid<Student> {

        public StudentsGrid() {
            var classColumn = this.addColumn(Student::getSchoolClass).setHeader("Class").setSortable(true);
            var lastNameColumn = this.addColumn(Student::getLastName).setHeader("Last Name").setSortable(true);
            var firstNameColumn = this.addColumn(Student::getFirstName).setHeader("First Name").setSortable(true);
            this.addColumn(Student::getToken).setHeader("Token");
            this.addColumn(Student::getRegistrationState).setHeader("Registration");

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
                    });

                    var bttDelete = new Button(VaadinIcon.TRASH.create());
                    bttDelete.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);

                    var layout = new HorizontalLayout(bttDelete, bttEdit);
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
        }

    }

}
