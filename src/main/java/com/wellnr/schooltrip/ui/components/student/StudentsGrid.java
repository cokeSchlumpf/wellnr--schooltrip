package com.wellnr.schooltrip.ui.components.student;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.SortDirection;
import com.wellnr.common.functions.Function1;
import com.wellnr.common.markup.Tuple2;
import com.wellnr.common.markup.Tuple3;
import com.wellnr.schooltrip.core.model.student.RegistrationState;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.questionaire.Ski;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridWithControls;

import java.util.List;

public class StudentsGrid extends ApplicationGridWithControls<Student> {

    public StudentsGrid() {

    }

    public Grid.Column<Student> addComponentColumnForRegisteredStudent(Function1<Student, Component> createComponent) {
        return this.getGrid().addComponentColumn(student -> {
            if (student.getRegistrationState().equals(RegistrationState.REGISTERED)) {
                return createComponent.get(student);
            } else {
                return new Span("-");
            }
        });
    }

    public Tuple3<Grid.Column<Student>, Grid.Column<Student>, Grid.Column<Student>> addDefaultColumns() {
        var classColumn = addClassColumn();
        var nameColumns = addNameColumns();

        return Tuple3.apply(
            classColumn, nameColumns._1, nameColumns._2
        );
    }

    public Tuple3<Grid.Column<Student>, Grid.Column<Student>, Grid.Column<Student>> addDefaultColumnsWithSorting() {
        var result = addDefaultColumns();

        this.getGrid().setMultiSort(true);
        this.getGrid().sort(List.of(
            new GridSortOrder<>(result._1, SortDirection.ASCENDING),
            new GridSortOrder<>(result._2, SortDirection.ASCENDING),
            new GridSortOrder<>(result._3, SortDirection.ASCENDING)
        ));

        return result;
    }

    public Grid.Column<Student> addDisciplineColumns() {
        return this
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
    }

    public Grid.Column<Student> addClassColumn() {
        return this
            .getGrid()
            .addColumn(Student::getSchoolClass)
            .setHeader("Class")
            .setSortable(true)
            .setFrozen(true)
            .setAutoWidth(true);
    }

    public Tuple2<Grid.Column<Student>, Grid.Column<Student>> addNameColumns() {
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

        return Tuple2.apply(lastNameColumn, firstNameColumn);
    }

    public Grid.Column<Student> addRegistrationStatusColumn() {
        return this
            .getGrid()
            .addComponentColumn(student -> {
                var span = new Span();
                span.setClassName("app--trip--registration-state--created");
                span.add(VaadinIcon.CHECK.create());
                return span;
            }).setHeader("Status");
    }

}
