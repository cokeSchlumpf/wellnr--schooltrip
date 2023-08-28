package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.infrastructure.SchoolTripCommandRunner;
import com.wellnr.schooltrip.infrastructure.UserSession;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridWithControls;
import com.wellnr.schooltrip.ui.components.student.StudentDetailsControl;
import org.springframework.beans.factory.annotation.Value;

import java.util.Objects;

public abstract class AbstractSchoolTripGridView extends AbstractSchoolTripView {

    @Value("${app.ui.base-url}")
    String appBaseUrl;

    protected ApplicationGridWithControls<Student> students;

    protected StudentDetailsControl studentDetails;

    public AbstractSchoolTripGridView(
        SchoolTripCommandRunner commandRunner, UserSession userSession
    ) {
        super(commandRunner, userSession);
    }

    @Override
    protected void updateView() {
        if (Objects.isNull(schoolTrip)) {
            return;
        }

        if (Objects.isNull(students)) {
            this.students = createStudentsGrid();
            this.students.getGrid().addSelectionListener(event -> {
                if (event.getFirstSelectedItem().isPresent()) {
                    studentDetails.setStudent(event.getFirstSelectedItem().get());
                } else {
                    studentDetails.close();
                }
            });
        }

        if (Objects.isNull(studentDetails)) {
            this.studentDetails = new StudentDetailsControl(
                appBaseUrl, schoolTrip.schoolTrip(), commandRunner
            );

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
        mainArea.setAlignItems(Alignment.STRETCH);
        mainArea.setFlexGrow(1, students);
        mainArea.setFlexGrow(0, studentDetails);
        mainArea.setWidthFull();

        mainArea.setMargin(false);
        mainArea.setPadding(false);
        mainArea.setSizeFull();

        this.removeAll();
        this.add(mainArea);
    }

    protected abstract ApplicationGridWithControls<Student> createStudentsGrid();

}