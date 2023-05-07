package com.wellnr.schooltrip.core.model.student;

import com.wellnr.ddd.AggregateRoot;
import com.wellnr.ddd.BeanValidation;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.schooltrip.repository.SchoolTripsReadRepository;
import com.wellnr.schooltrip.core.model.student.student.StudentRegisteredEvent;
import com.wellnr.schooltrip.core.model.user.AuthenticatedUser;
import com.wellnr.schooltrip.core.model.user.DomainPermissions;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Student extends AggregateRoot<String, Student> {

    private final String id;

    private final SchoolTripId schoolTrip;

    String schoolClass;

    String firstName;

    String lastName;

    Instant birthday;

    Gender gender;

    /**
     * Creates a new instance of this entity.
     *
     * @param schoolClass The name of the school class to which this student belongs to.
     * @param firstName   The first name of the student.
     * @param lastName    The full name of this student.
     * @param birthday    The birthday of the student.
     * @param gender      The hender of the student.
     * @return The new instance.
     */
    public static Student createNew(
        SchoolTripId schoolTrip,
        String schoolClass,
        String firstName,
        String lastName,
        Instant birthday,
        Gender gender) {

        var id = UUID.randomUUID().toString();
        return new Student(id, schoolTrip, schoolClass, firstName, lastName, birthday, gender);
    }

    /**
     * Initially creates (persists) this student class.
     *
     * @param creator The user who wants to create the school trip.
     * @param repository The repository to persist the information.
     */
    public void register(
        User creator,
        SchoolTripsReadRepository schoolTrips,
        StudentsRepository repository,
        BeanValidation validation) {

        var existing = repository.findStudentBySchoolTripAndSchoolClassNameAndFirstNameAndLastName(
            this.schoolTrip,
            this.schoolClass,
            this.firstName,
            this.lastName
        );

        // Will throw an exception if school class not found.
        var schoolTrip = schoolTrips.getSchoolTripById(this.schoolTrip);
        schoolTrip.getSchoolClassByName(this.schoolClass);

        /*
         * Validate if creator is allowed.
         */
        creator.checkPermission(
            DomainPermissions.APPLICATION__MANAGE_TRIPS,
            DomainPermissions.TRIPS__MANAGE_TRIP.onSubject(schoolTrip.getUri())
        );

        /*
         * Validate Properties.
         */
        validation.validateObject(this);

        /*
         * Actual logic:
         * - Check whether trip already exists (from a different entity).
         * - If it does not exist, create.
         * - If it exists for the current entity, do nothing.
         */
        if (existing.isPresent() && !existing.get().id.equals(this.id)) {
            throw StudentAlreadyExistsException.apply();
        } else if (existing.isEmpty()) {
            this.registerEvent(StudentRegisteredEvent.apply(this));
            repository.insertOrUpdateStudent(this);
        }
    }

    @Override
    public String getId() {
        return id;
    }

}
