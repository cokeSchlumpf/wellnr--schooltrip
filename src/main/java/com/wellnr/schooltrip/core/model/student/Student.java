package com.wellnr.schooltrip.core.model.student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wellnr.ddd.AggregateRoot;
import com.wellnr.ddd.BeanValidation;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.schooltrip.repository.SchoolTripsReadRepository;
import com.wellnr.schooltrip.core.model.student.events.StudentRegisteredEvent;
import com.wellnr.schooltrip.core.model.student.questionaire.Questionaire;
import com.wellnr.schooltrip.core.model.user.DomainPermissions;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Getter
@ToString
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

    RegistrationState registrationState;

    String token;

    String confirmationToken;

    Questionaire questionaire;

    String notificationEmail;

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
        var token = RandomStringUtils.randomAlphanumeric(8);
        var confirmationToken = RandomStringUtils.randomAlphanumeric(8);

        return new Student(
            id, schoolTrip, schoolClass, firstName, lastName, birthday, gender,
            RegistrationState.CREATED, token, confirmationToken, null, null
        );
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

    @JsonIgnore
    public String getDisplayName() {
        return String.format("%s %s", firstName, lastName);
    }

    @Override
    public String getId() {
        return id;
    }

    public Optional<Questionaire> getQuestionaire() {
        return Optional.of(questionaire);
    }

    public void completeStudentRegistration(
        Questionaire questionaire,
        String notificationEmail,
        StudentsRepository students) {

        this.questionaire = questionaire;
        this.registrationState = RegistrationState.WAITING_FOR_CONFIRMATION;
        this.notificationEmail = notificationEmail;

        // TODO: Send E-Mail with confirmation link.
        students.insertOrUpdateStudent(this);
    }

    public void confirmStudentRegistration(StudentsRepository students) {
        this.registrationState = RegistrationState.REGISTERED;
        students.insertOrUpdateStudent(this);
    }

}
