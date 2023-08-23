package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.questionaire.Ski;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.ui.components.student.StudentDetailsControl;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridWithControls;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;

import java.util.List;
import java.util.Objects;

@Route(value = "trips/:name", layout = ApplicationAppLayout.class)
public class SchoolTripView extends AbstractSchoolTripView {

    private ApplicationGridWithControls<Student> students;

    private StudentDetailsControl studentDetails;

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

        if (Objects.isNull(students)) {
            this.students = new StudentsGrid();
        }

        if (Objects.isNull(studentDetails)) {
            this.studentDetails = new StudentDetailsControl(schoolTrip.schoolTrip(), commandRunner);
            this.studentDetails.addStudentDetailsUpdatedListener(event -> this.reload());
        }

        /*
         * Update view.
         */
        // Remember the current selected student.
        var selectedStudent = this.students
            .getGrid()
            .getSelectedItems()
            .stream()
            .findFirst();

        // Updte the grid. This will remove selection.
        this.students.getGrid().setItems(schoolTrip.students());

        // Check whether previously selected student is still present, if yes, select.
        if (selectedStudent.isPresent()) {
            var s = selectedStudent.get();

            var newSelectedStudent = schoolTrip
                .students()
                .stream()
                .filter(student -> student.getId().equals(s.getId()))
                .findFirst();

            if (newSelectedStudent.isPresent()) {
                students.getGrid().select(newSelectedStudent.get());
            } else {
                studentDetails.close();
            }
        } else {
            studentDetails.close();
        }

        /*
         * Layout
         */
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
                        studentDetails.setStudent(student);
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
                    studentDetails.setStudent(event.getFirstSelectedItem().get());
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
