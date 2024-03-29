package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.wellnr.common.functions.Function1;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.grid.ApplicationGridWithControls;
import com.wellnr.schooltrip.ui.components.student.StudentDetailsControl;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractSchoolTripGridView extends AbstractSchoolTripView {

    private final SchoolTripMessages i18n;

    protected ApplicationGridWithControls<Student> students;

    protected StudentDetailsControl studentDetails;

    protected Function1<Student, Boolean> filterStudents;

    @Value("${app.ui.base-url}")
    String appBaseUrl;

    public AbstractSchoolTripGridView(
        ApplicationCommandRunner commandRunner, ApplicationUserSession userSession
    ) {
        super(commandRunner, userSession);
        this.i18n = userSession.getMessages();
        this.filterStudents = student -> true;
    }

    protected abstract ApplicationGridWithControls<Student> createStudentsGrid();

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
                appBaseUrl, schoolTrip.schoolTrip(), commandRunner, i18n
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

        // Update the grid. This will remove selection.
        var allStudents = schoolTrip.students().stream().filter(s -> filterStudents.get(s)).collect(Collectors.toList());
        this.students
                .getGrid()
                .setItems(allStudents);

        // Check whether previously selected student is still present, if yes, select.
        if (selectedStudent.isPresent()) {
            var s = selectedStudent.get();

            var newSelectedStudent = allStudents
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

}
