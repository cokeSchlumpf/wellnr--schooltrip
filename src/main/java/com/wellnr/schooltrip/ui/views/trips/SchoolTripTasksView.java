package com.wellnr.schooltrip.ui.views.trips;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.wellnr.common.Operators;
import com.wellnr.common.functions.Procedure0;
import com.wellnr.schooltrip.core.application.commands.schooltrip.CloseSchoolTripRegistrationCommand;
import com.wellnr.schooltrip.core.application.commands.schooltrip.ReassignSchoolTripStudentIdsCommand;
import com.wellnr.schooltrip.core.application.commands.students.RegisterStudentCommand;
import com.wellnr.schooltrip.core.application.commands.students.RegisterStudentsCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.student.Gender;
import com.wellnr.schooltrip.infrastructure.ApplicationCommandRunner;
import com.wellnr.schooltrip.infrastructure.ApplicationUserSession;
import com.wellnr.schooltrip.ui.components.ApplicationCard;
import com.wellnr.schooltrip.ui.components.ExcelImportDialog;
import com.wellnr.schooltrip.ui.layout.ApplicationAppLayout;
import lombok.Data;
import org.apache.commons.lang3.time.DateUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Route(value = "trips/:name/tasks", layout = ApplicationAppLayout.class)
public class SchoolTripTasksView extends AbstractSchoolTripView {

    public SchoolTripTasksView(
        ApplicationCommandRunner commandRunner, ApplicationUserSession userSession
    ) {
        super(commandRunner, userSession);
    }

    /**
     * Helper method to generate the route parameters for this view.
     *
     * @param name The name of the school trip to route to.
     * @return The set of route parameters required to call this view.
     */
    public static RouteParameters getRouteParameters(String name) {
        return SchoolTripView.getRouteParameters(name);
    }

    @Override
    protected void updateView() {
        this.removeAll();
        this.add(new HorizontalLayout(
            new TaskCard("Import Students", "Import students from an Excel file.", this::runExcelImport),
            new TaskCard("Close Registration", "Close registration and remove studens who are not registered.", this::closeRegistration),
            new TaskCard("(Re-)Assign IDs", "Re-assign increasing ID to registered students.", this::reassignStudentIDs)
        ));
    }

    private void reassignStudentIDs() {
        commandRunner.runAndNotify(ReassignSchoolTripStudentIdsCommand.apply(
            new SchoolTripId(this.schoolTrip.schoolTrip().getId())
        ));
    }

    private void closeRegistration() {
        commandRunner.runAndNotify(CloseSchoolTripRegistrationCommand.apply(
            new SchoolTripId(this.schoolTrip.schoolTrip().getId())
        ));
    }

    private void runExcelImport() {
        var dialog = ExcelImportDialog.apply(
            List.of("Klasse", "Vorname", "Nachname", "Date of Birth", "Gender"),
            (parameters, settings) -> {
                var schoolClass = parameters.get(0).toString();
                var firstName = parameters.get(1).toString();
                var lastName = parameters.get(2).toString();
                var dateOfBirthDate = parameters.get(3);
                var genderStr = parameters.get(4).toString();
                var dateOfBirth = LocalDate.now();
                var gender = Gender.NotSpecified;

                if (dateOfBirthDate instanceof Date dt) {
                    var instant = DateUtils.toCalendar(dt).toInstant();
                    dateOfBirth = LocalDate.ofInstant(instant, ZoneId.systemDefault());
                }

                if (Operators.fuzzyEquals(genderStr, settings.genderFemalesPattern)) {
                    gender = Gender.Female;
                } else if (Operators.fuzzyEquals(genderStr, settings.genderMalePattern)) {
                    gender = Gender.Male;
                }

                return RegisterStudentCommand.apply(
                    schoolTrip.schoolTrip().getName(), schoolClass, firstName, lastName,
                    dateOfBirth, gender
                );
            },
            new ExcelImportSettings()
        );

        dialog.addDataImportedListener(imported -> {
            var commands = imported.getResult();
            var command = RegisterStudentsCommand.apply(schoolTrip.schoolTrip().getName(), commands);
            commandRunner.runAndNotify(command);
        });

        this.add(dialog);
        dialog.open();
    }

    private static class TaskCard extends ApplicationCard {

        public TaskCard(String title, String description, Procedure0 runTask) {
            this.add(new H4(title));
            this.add(new Paragraph(description));

            var btt = new Button("Run task");
            btt.addClickListener(event -> runTask.run());
            this.add(btt);
        }

    }

    @Data
    public static class ExcelImportSettings {

        String genderMalePattern = "m";

        String genderFemalesPattern = "w";

    }

}
