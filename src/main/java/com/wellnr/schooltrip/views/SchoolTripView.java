package com.wellnr.schooltrip.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.wellnr.ddd.DomainException;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.RegisterSchoolClassCommand;
import com.wellnr.schooltrip.core.application.commands.RegisterStudentCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.schooltrip.exceptions.SchoolClassAlreadyExistsException;
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

    private SchoolTrip schoolTrip;

    private final Text name;

    public SchoolTripView(UserSession userSession, SchoolTripDomainRegistry domainRegistry) {
        this.domainRegistry = domainRegistry;
        this.userSession = userSession;
        this.name = new Text("");

        this.add(this.name);

        /*
        var buffer = new MemoryBuffer();
        var upload = new Upload(buffer);

        upload.addSucceededListener(event -> {
            var dialog = new ExcelImportDialog(buffer.getInputStream());
            this.add(dialog);
            dialog.open();
        });

        this.add(upload);
         */

        var button = new Button("Upload");
        button.addClickListener(event -> {
            var dialog = new ExcelImportDialog<>(
                List.of("Klasse", "Vorname", "Nachname"),
                parameters -> {
                    var schoolClass = parameters.get(0).toString();
                    var firstName = parameters.get(1).toString();
                    var lastName = parameters.get(2).toString();

                    return RegisterStudentCommand.apply(
                        schoolTrip.getName(), schoolClass, firstName, lastName
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
                        schoolTrip.getName(),
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
            });

            this.add(dialog);
            dialog.open();
        });

        this.add(button);
    }

    @Override
    public String getSectionTitle() {
        if (Objects.isNull(schoolTrip)) {
            return "School Trip";
        } else {
            return this.schoolTrip.getTitle();
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // TODO Not found?
        var name = beforeEnterEvent
            .getRouteParameters()
            .get("name")
            .orElseThrow();

        this.schoolTrip = domainRegistry.getSchoolTrips().getSchoolTripByName(name);
    }

    private void updateView() {
        if (Objects.isNull(this.schoolTrip)) {
            return;
        }

        this.name.setText(this.schoolTrip.getName());
    }
}
