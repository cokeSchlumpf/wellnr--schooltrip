package com.wellnr.schooltrip.core.model.schooltrip;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wellnr.common.Operators;
import com.wellnr.ddd.AggregateRoot;
import com.wellnr.ddd.BeanValidation;
import com.wellnr.schooltrip.core.application.SchoolTripApplicationConfiguration;
import com.wellnr.schooltrip.core.model.schooltrip.events.*;
import com.wellnr.schooltrip.core.model.schooltrip.exceptions.SchoolClassNotFoundException;
import com.wellnr.schooltrip.core.model.schooltrip.exceptions.SchoolTripAlreadyExistsException;
import com.wellnr.schooltrip.core.model.schooltrip.repository.SchoolTripsRepository;
import com.wellnr.schooltrip.core.model.student.RegistrationState;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.StudentId;
import com.wellnr.schooltrip.core.model.student.StudentsReadRepository;
import com.wellnr.schooltrip.core.model.user.*;
import com.wellnr.schooltrip.core.model.user.rbac.DomainPermissions;
import com.wellnr.schooltrip.core.utils.FileZipper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SchoolTrip extends AggregateRoot<String, SchoolTrip> {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String TITLE = "title";
    private static final String SETTINGS = "settings";
    private static final String SCHOOL_CLASSES = "schoolClasses";
    private static final String ID_ASSIGNMENTS = "idAssignments";
    private static final String MANAGERS = "managers";

    @JsonProperty(ID)
    private final String id;

    @JsonProperty(TITLE)
    private String title;

    @JsonProperty(NAME)
    private String name;

    @JsonProperty(SETTINGS)
    private SchoolTripSettings settings;

    @JsonProperty(SCHOOL_CLASSES)
    private Set<SchoolClass> schoolClasses;

    @JsonProperty(ID_ASSIGNMENTS)
    private Map<Integer, StudentId> studentIdAssignments;

    @JsonProperty(MANAGERS)
    private Set<RegisteredUserId> managers;

    /**
     * Creates an instance of this entity class.
     *
     * @param title A human-readable title for the school trip.
     * @param name  The technical name of the scool trip.
     * @return An instance of the entity.
     */
    public static SchoolTrip create(
        @NotNull @NotBlank String title,
        String name) {

        var id = UUID.randomUUID().toString();

        if (Objects.isNull(name)) {
            name = Operators.stringToTechFriendlyName(title);
        }

        return new SchoolTrip(
            id, title, name, SchoolTripSettings.apply(), new HashSet<>(), new HashMap<>(), new HashSet<>()
        );
    }

    /**
     * Creates an instance of this entity class.
     *
     * @param title A human-readable title for the school trip.
     * @return An instance of the entity.
     */
    public static SchoolTrip create(
        @NotNull @NotBlank String title
    ) {
        return create(title, null);
    }

    /**
     * Adds a user who can manage this school trip.
     *
     * @param executor    The user executing the action.
     * @param email       The email address of the user who should become a manager.
     * @param schoolTrips The repository to read/write entity information.
     * @param users       The repository to read user information.
     */
    public RegisteredUser addManager(
        User executor, String email, SchoolTripsRepository schoolTrips,
        RegisteredUsersReadRepository users) {

        executor.checkPermission(
            DomainPermissions.ManageSchoolTrips.apply()
        );

        var user = users.getOneByEmail(email);
        var userId = new RegisteredUserId(user.getId());
        this.managers.add(userId);

        this.registerEvent(SchoolTripManagerAddedEvent.apply(this, userId));
        schoolTrips.save(this);

        return user;
    }

    /**
     * Checks whether a user can see the school trip.
     *
     * @param user The user for which access should be checked.
     * @return True if user can see the school trip, false otherwise.
     */
    public boolean canBeAccessedByUser(User user) {
        return user.hasPermission(
            DomainPermissions.ManageSchoolTrips.apply(),
            DomainPermissions.ManageSchoolTrip.apply(this.id)
        );
    }

    /**
     * This operation creates an Excel file containing the student data for the invitation
     * mailing. The file returned will be a ZIP-file the Excel file and QR-code images.
     *
     * @param executor The user executing the operation.
     * @param students The repository to read/write student information.
     * @param config   The configuration of the application.
     * @return The exported data.
     */
    public Path exportInviteLetterMailingData(
        User executor, StudentsReadRepository students, SchoolTripApplicationConfiguration config
    ) {

        // Check permissions
        executor.checkPermission(
            DomainPermissions.ManageSchoolTrips.apply(),
            DomainPermissions.ManageSchoolTrip.apply(id)
        );

        // Create data.
        var tmpOutputFile = Operators.suppressExceptions(
            () -> Files.createTempFile("schooltrips", ".zip")
        );

        var tmpDirectory = Operators.suppressExceptions(
            () -> Files.createTempDirectory("schooltrips")
        );

        var qrCodesDir = Operators.suppressExceptions(
            () -> Files.createDirectory(tmpDirectory.resolve("QRCodes"))
        );

        /*
         * Create Excel file and QR Code images.
         */
        var excelFile = tmpDirectory.resolve("students.xlsx");
        var workbook = Operators.suppressExceptions(() -> new XSSFWorkbook());
        var sheet = workbook.createSheet("Schüler");

        var row = sheet.createRow(0);
        row.createCell(0).setCellValue("School Class");
        row.createCell(1).setCellValue("Last Name");
        row.createCell(2).setCellValue("First Name");
        row.createCell(3).setCellValue("Token");
        row.createCell(4).setCellValue("Registration Link");
        row.createCell(5).setCellValue("Registration QR-Code");

        var allStudents = students
            .findStudentsBySchoolTrip(new SchoolTripId(this.id))
            .stream()
            .sorted(
                Comparator
                    .comparing(Student::getSchoolClass)
                    .thenComparing(Student::getLastName)
                    .thenComparing(Student::getFirstName)
            )
            .toList();

        for (var i = 0; i < allStudents.size(); i++) {
            var student = allStudents.get(i);

            // Create QR-Code
            var qrCodeFileName =
                student.getSchoolClass() + "--" + Operators.stringToKebabCase(student.getLastName()) + "--" + Operators.stringToKebabCase(student.getFirstName()) + ".png";

            var qrCodeLink = config.getUi().getBaseUrl() + "students/complete-registration/" + student.getToken();

            var qrCodeFile = QRCode
                .from(qrCodeLink)
                .withSize(500, 500)
                .to(ImageType.PNG)
                .file();

            Operators.suppressExceptions(
                () -> Files.move(qrCodeFile.toPath(), qrCodesDir.resolve(qrCodeFileName))
            );

            // Create row in Excel
            row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(student.getSchoolClass());
            row.createCell(1).setCellValue(student.getLastName());
            row.createCell(2).setCellValue(student.getFirstName());
            row.createCell(3).setCellValue(student.getToken());
            row.createCell(4).setCellValue(qrCodeLink);
            row.createCell(5).setCellValue(qrCodeFileName);
        }

        // Save Excel Workbook
        try (var fos = new FileOutputStream(excelFile.toFile())) {
            workbook.write(fos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // ZIP directory and save as temporary file
        FileZipper.zip(tmpDirectory, tmpOutputFile, "invitation-mailing");
        Operators.ignoreExceptions(() -> FileUtils.deleteDirectory(tmpDirectory.toFile()));

        return tmpOutputFile;
    }

    /**
     * Removes a manager from the school trip.
     *
     * @param executor    The user executing the operation.
     * @param userId      The user who should be removed as manager.
     * @param schoolTrips The repository to read/ write entity information.
     */
    public void removeManager(
        User executor, RegisteredUserId userId, SchoolTripsRepository schoolTrips
    ) {
        /*
         * Only administratirs can remove managers for school trips.
         */
        executor.checkPermission(
            DomainPermissions.ManageSchoolTrips.apply()
        );

        this.managers.remove(userId);
        this.registerEvent(SchoolTripManagerRemovedEvent.apply(this, userId));
        schoolTrips.save(this);
    }

    /**
     * (Re-)assigns student numbers/ IDs to students.
     *
     * @param executor    The user executing the action.
     * @param schoolTrips The repository to read/ write trip information.
     * @param students    The rpeository to read student information.
     */
    public void assignStudentIDs(User executor, SchoolTripsRepository schoolTrips, StudentsReadRepository students) {
        var id = 1;
        this.studentIdAssignments.clear();

        var classesSorted = this
            .schoolClasses
            .stream()
            .map(SchoolClass::getName)
            .sorted()
            .toList();

        for (String sc : classesSorted) {
            var scStudents = students
                .findStudentsBySchoolTripAndSchoolClassName(new SchoolTripId(this.id), sc)
                .stream()
                .filter(s -> s.getRegistrationState().equals(RegistrationState.REGISTERED))
                .sorted(Comparator.comparing(Student::getLastName).thenComparing(Student::getLastName))
                .toList();

            for (var student : scStudents) {
                var studentId = new StudentId(student.getId());
                this.studentIdAssignments.put(id, studentId);
                registerEvent(StudentIDAssigndEvent.apply(new SchoolTripId(this.id), studentId, id));
                id = id + 1;
            }
        }

        schoolTrips.save(this);
    }

    /**
     * Returns a list of all students which are assigned to this school trip.
     *
     * @param executor The user requesting the information.
     * @param students The repository to read students from.
     * @return A list of students.
     */
    public List<Student> getRegisteredStudents(User executor, StudentsReadRepository students) {
        /*
         * Validate Permissions
         */
        executor.hasPermission(
            DomainPermissions.ManageSchoolTrips.apply(),
            DomainPermissions.ManageSchoolTrip.apply(this.id)
        );

        /*
         * Return results.
         */
        return students.findStudentsBySchoolTrip(new SchoolTripId(this.getId()));
    }

    /**
     * This operation closes the registration of the school trip. This includes the removal of
     * all students who have not completed registration. And assignment of increasing order number
     * of students.
     *
     * @param executor    The user who is executing teh action.
     * @param schoolTrips The repository to store trip information.
     * @param students    The repository to read student information.
     */
    public List<Student> closeRegistration(
        User executor, SchoolTripsRepository schoolTrips, StudentsReadRepository students
    ) {
        /*
         * Remove students who are not registered yet.
         */
        var allStudents = students.findStudentsBySchoolTrip(new SchoolTripId(this.id));

        var notRegisteredStudents = allStudents
            .stream()
            .filter(s -> !s.getRegistrationState().equals(RegistrationState.REGISTERED))
            .collect(Collectors.toList());

        for (var s : notRegisteredStudents) {
            Operators.ignoreExceptions(() -> {
                var id = new StudentId(s.getId());

                this.getSchoolClassByName(s.getSchoolClass()).getStudents().remove(id);

                registerEvent(StudentRemovedFromSchoolTripEvent.apply(
                    new SchoolTripId(this.id), id
                ));
            });
        }

        this.assignStudentIDs(executor, schoolTrips, students);
        return notRegisteredStudents;
    }

    /**
     * Initially creates (persists) this school trip.
     *
     * @param creator    The user who wants to create the school trip.
     * @param repository The repository to persist the information.
     */
    public void create(User creator, SchoolTripsRepository repository, BeanValidation validation) {
        var existing = repository.findSchoolTripByName(this.name);

        /*
         * Validate if creator is allowed.
         */
        creator.checkPermission(DomainPermissions.ManageSchoolTrips.apply());

        /*
         * Validate Properties. TODO: Execute by Proxy (on every void method?)
         */
        validation.validateObject(this);

        /*
         * Actual logic:
         * - Check whether trip already exists (from a different entity).
         * - If it does not exist, create.
         * - If it exists for the current entity, do nothing.
         */
        if (existing.isPresent() && !existing.get().id.equals(this.id)) {
            throw SchoolTripAlreadyExistsException.apply(this.name);
        } else if (existing.isEmpty()) {
            this.registerEvent(SchoolTripCreatedEvent.apply(this));

            if (creator.getRegisteredUser().isPresent()) {
                var user = creator.getRegisteredUser().get();
                var userId = new RegisteredUserId(user.getId());
                this.registerEvent(SchoolTripManagerAddedEvent.apply(this, userId));
                this.managers.add(userId);
            }

            repository.save(this);
        }
    }

    /**
     * Register a new school class attending the trip.
     *
     * @param executor   The user who adds the school class to this trip.
     * @param name       The name of the school class - Must be unique for the trip.
     * @param repository The repository from the domain registry.
     */
    public void registerSchoolClass(
        User executor, String name, SchoolTripsRepository repository) {

        var exists = this.findSchoolClassByName(name);

        if (exists.isEmpty()) {
            var schoolClass = SchoolClass.apply(name);

            this.registerEvent(SchoolClassRegisteredEvent.apply(
                new SchoolTripId(this.id), schoolClass
            ));

            this.schoolClasses.add(schoolClass);
            repository.save(this);
        }
    }

    /**
     * Registers a new student as member of a school class and trip.
     *
     * @param schoolClass The school class the student belongs to.
     * @param student     The student which has been registered.
     */
    public void registerStudent(
        String schoolClass, StudentId student, SchoolTripsRepository schoolTrips
    ) {
        this.getSchoolClassByName(schoolClass).getStudents().add(student);
        schoolTrips.save(this);
    }

    /**
     * Updates a students membership of a class.
     *
     * @param oldClass    The old class he was part of until now.
     * @param newClass    The new class he is now part of.
     * @param student     The student id.
     * @param schoolTrips The repository to store the information.
     */
    public void updateStudentsClass(
        String oldClass, String newClass, StudentId student, SchoolTripsRepository schoolTrips
    ) {
        this.getSchoolClassByName(oldClass).getStudents().remove(student);
        this.getSchoolClassByName(newClass).getStudents().add(student);
        schoolTrips.save(this);
    }

    /**
     * Rename the title of the school trip.
     *
     * @param executor    The user executing this action.
     * @param newTitle    The new title of the trip.
     * @param schoolTrips The repository to store the information.
     */
    public void rename(User executor, String newTitle, SchoolTripsRepository schoolTrips) {
        /*
         * Validate Permissions
         */
        executor.hasPermission(
            DomainPermissions.ManageSchoolTrips.apply(),
            DomainPermissions.ManageSchoolTrip.apply(id)
        );

        this.title = newTitle;
        schoolTrips.save(this);
    }

    /**
     * Get a school class by its name.
     *
     * @param name The name of the class.
     * @return The class, if found.
     */
    public Optional<SchoolClass> findSchoolClassByName(String name) {
        return this
            .schoolClasses
            .stream()
            .filter(cls -> cls.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    public List<RegisteredUser> getManagers(RegisteredUsersRepository users) {
        return this
            .getManagers()
            .stream()
            .map(id -> users.findOneById(id.id()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    /**
     * Get a school class by its name, throw if class not found.
     *
     * @param name The name of the class.
     * @return The class.
     */
    public SchoolClass getSchoolClassByName(String name) {
        return this.findSchoolClassByName(name).orElseThrow(
            () -> SchoolClassNotFoundException.apply(name)
        );
    }

    /**
     * Updates the school trips settings.
     *
     * @param settings    The updated settings.
     * @param schoolTrips The repository to persist the information.
     */
    public void updateSettings(SchoolTripSettings settings, SchoolTripsRepository schoolTrips) {
        this.settings = settings;
        schoolTrips.save(this);
    }

    @Override
    @JsonIgnore
    public URI getUri() {
        return URI.create(MessageFormat.format(
            "urn:schoolclass:{0}", this.id
        ));
    }

}
