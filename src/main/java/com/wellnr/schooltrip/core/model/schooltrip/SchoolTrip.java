package com.wellnr.schooltrip.core.model.schooltrip;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wellnr.common.Operators;
import com.wellnr.ddd.AggregateRoot;
import com.wellnr.ddd.BeanValidation;
import com.wellnr.schooltrip.core.model.schooltrip.events.SchoolClassRegisteredEvent;
import com.wellnr.schooltrip.core.model.schooltrip.events.SchoolTripCreatedEvent;
import com.wellnr.schooltrip.core.model.schooltrip.exceptions.SchoolTripAlreadyExistsException;
import com.wellnr.schooltrip.core.model.schooltrip.repository.SchoolTripsRepository;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.StudentId;
import com.wellnr.schooltrip.core.model.student.StudentsReadRepository;
import com.wellnr.schooltrip.core.model.user.DomainPermissions;
import com.wellnr.schooltrip.core.model.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;
import java.text.MessageFormat;
import java.util.*;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SchoolTrip extends AggregateRoot<String, SchoolTrip> {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String TITLE = "title";
    private static final String SETTINGS = "settings";
    private static final String SCHOOL_CLASSES = "schoolClasses";


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

        return new SchoolTrip(id, title, name, SchoolTripSettings.apply(), new HashSet<>());
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

    public List<Student> getRegisteredStudents(User executor, StudentsReadRepository students) {
        /*
         * Validate Permissions
         */
        executor.hasPermission(
            DomainPermissions.APPLICATION__MANAGE_TRIPS,
            DomainPermissions.TRIPS__MANAGE_TRIP.onSubject(this.getUri())
        );

        /*
         * Return results.
         */
        return students.findStudentsBySchoolTrip(new SchoolTripId(this.getId()));
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
        creator.checkPermission(DomainPermissions.APPLICATION__MANAGE_TRIPS);

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
            DomainPermissions.APPLICATION__MANAGE_TRIPS,
            DomainPermissions.TRIPS__MANAGE_TRIP.onSubject(this.getUri())
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

    /**
     * Get a school class by its name, throw if class not found.
     *
     * @param name The name of the class.
     * @return The class.
     */
    public SchoolClass getSchoolClassByName(String name) {
        return this.findSchoolClassByName(name).orElseThrow(); // TODO mw: Nice exception.
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
