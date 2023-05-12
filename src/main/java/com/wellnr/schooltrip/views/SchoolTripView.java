package com.wellnr.schooltrip.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.wellnr.ddd.DomainException;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.GetSchoolTripDetailsCommand;
import com.wellnr.schooltrip.core.application.commands.RegisterSchoolClassCommand;
import com.wellnr.schooltrip.core.application.commands.RegisterStudentCommand;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.infrastructure.UserSession;
import com.wellnr.schooltrip.views.components.Container;
import com.wellnr.schooltrip.views.components.ExcelImportDialog;
import com.wellnr.schooltrip.views.layout.SchoolTripAppLayout;
import com.wellnr.schooltrip.views.layout.SchoolTripAppView;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Route(value = "trips/:name", layout = SchoolTripAppLayout.class)
public class SchoolTripView extends Container implements SchoolTripAppView, BeforeEnterObserver {

    private final SchoolTripDomainRegistry domainRegistry;

    private final UserSession userSession;

    private GetSchoolTripDetailsCommand.SchoolTripDetailsProjection schoolTrip;

    private String name;

    private final Grid<Student> students;

    public SchoolTripView(UserSession userSession, SchoolTripDomainRegistry domainRegistry) {
        this.domainRegistry = domainRegistry;
        this.userSession = userSession;

        this.add(this.name);

        var button = new Button("Upload");
        button.addClickListener(event -> {
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

            dialog.addDataImportedListener(imported -> {
                var students = imported.getResult();
                var user = userSession.getRegisteredUser().orElseGet(
                    () -> domainRegistry.getUsers().getOneByEmail("michael.wellner@gmail.com")
                );

                /*
                 * Create classes.
                 */
                students
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
                            var result = cmd.run(user, domainRegistry);
                            System.out.println(result.getMessage());
                        } catch (DomainException ex) {
                            System.out.println(ex.getSummary());
                        }
                    });

                /*
                 * Register students.
                 */
                students.forEach(cmd -> {
                    try {
                        var result = cmd.run(user, domainRegistry);
                        System.out.println(result.getMessage());
                    } catch (DomainException ex) {
                        System.out.println(ex.getSummary());
                    }
                });

                this.updateView();
            });

            this.add(dialog);
            dialog.open();
        });

        this.add(button);

        this.students = new Grid<>();
        var classColumn = this.students.addColumn(Student::getSchoolClass).setHeader("Class").setSortable(true);
        var lastNameColumn = this.students.addColumn(Student::getLastName).setHeader("Last Name").setSortable(true);
        var firstNameColumn = this.students.addColumn(Student::getFirstName).setHeader("First Name").setSortable(true);
        this.students.addColumn(Student::getToken).setHeader("Token");
        this.students.addColumn(Student::getRegistrationState).setHeader("Registration");
        this.students.setMultiSort(true);

        this.students.sort(List.of(
            new GridSortOrder<>(classColumn, SortDirection.ASCENDING),
            new GridSortOrder<>(lastNameColumn, SortDirection.ASCENDING),
            new GridSortOrder<>(firstNameColumn, SortDirection.ASCENDING)
        ));

        this.add(this.students);
        this.updateView();
    }

    @Override
    public String getSectionTitle() {
        if (Objects.isNull(schoolTrip)) {
            return "School Trip";
        } else {
            return this.schoolTrip.schoolTrip().getTitle();
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        this.name = beforeEnterEvent
            .getRouteParameters()
            .get("name")
            .orElseThrow();

        updateView();
    }

    private void updateView() {
        if (Objects.isNull(this.name)) {
            return;
        }

        var cmd = GetSchoolTripDetailsCommand.apply(name);
        this.schoolTrip = cmd
            .run(
                userSession.getRegisteredUser().orElseGet(
                    () -> domainRegistry.getUsers().getOneByEmail("michael.wellner@gmail.com")
                ),
                domainRegistry
            )
            .getData();

        this.students.setItems(schoolTrip.students());
    }
}
