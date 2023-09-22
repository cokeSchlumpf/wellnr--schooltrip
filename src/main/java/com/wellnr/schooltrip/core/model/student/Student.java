package com.wellnr.schooltrip.core.model.student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wellnr.common.Operators;
import com.wellnr.common.markup.Either;
import com.wellnr.ddd.AggregateRoot;
import com.wellnr.ddd.BeanValidation;
import com.wellnr.schooltrip.core.application.SchoolTripApplicationConfiguration;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.schooltrip.repository.SchoolTripsReadRepository;
import com.wellnr.schooltrip.core.model.student.events.StudentRegisteredEvent;
import com.wellnr.schooltrip.core.model.student.events.StudentsSchoolClassChangedEvent;
import com.wellnr.schooltrip.core.model.student.exceptions.StudentAlreadyExistsException;
import com.wellnr.schooltrip.core.model.student.payments.Payment;
import com.wellnr.schooltrip.core.model.student.payments.Payments;
import com.wellnr.schooltrip.core.model.student.payments.PriceLineItem;
import com.wellnr.schooltrip.core.model.student.payments.PriceLineItems;
import com.wellnr.schooltrip.core.model.student.questionaire.Questionnaire;
import com.wellnr.schooltrip.core.model.student.questionaire.Ski;
import com.wellnr.schooltrip.core.model.student.questionaire.Snowboard;
import com.wellnr.schooltrip.core.model.user.User;
import com.wellnr.schooltrip.core.model.user.rbac.DomainPermissions;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    LocalDate birthday;

    Gender gender;

    RegistrationState registrationState;

    String token;

    String confirmationToken;

    Questionnaire questionnaire;

    String notificationEmail;

    List<Payment> payments;

    Integer tripStudentId;

    public static PriceLineItems calculatePriceLineItems(
        SchoolTrip schoolTrip, Questionnaire questionnaire, SchoolTripMessages i18n
    ) {
        var lineItems = new ArrayList<PriceLineItem>();

        lineItems.add(new PriceLineItem(
            i18n.basePrice(), schoolTrip.getSettings().getBasePrice()
        ));

        if (questionnaire.getDisziplin() instanceof Ski ski) {
            if (ski.getRental().isPresent()) {
                lineItems.add(new PriceLineItem(
                    i18n.skiRental(), schoolTrip.getSettings().getSkiRentalPrice()
                ));
            }

            if (ski.getBootRental().isPresent()) {
                lineItems.add(new PriceLineItem(
                    i18n.skiBootRental(), schoolTrip.getSettings().getSkiBootsRentalPrice()
                ));
            }
        }

        if (questionnaire.getDisziplin() instanceof Snowboard sb) {
            if (sb.getRental().isPresent()) {
                lineItems.add(new PriceLineItem(
                    i18n.snowboardRental(), schoolTrip.getSettings().getSnowboardRentalPrice()
                ));
            }

            if (sb.getBootRental().isPresent()) {
                lineItems.add(new PriceLineItem(
                    i18n.snowboardBootRental(), schoolTrip.getSettings().getSnowboardBootsRentalPrice()
                ));
            }
        }

        if (questionnaire.getDisziplin().hasHelmRental()) {
            lineItems.add(new PriceLineItem(
                i18n.helmetRental(), schoolTrip.getSettings().getHelmetRentalPrice()
            ));
        }

        return PriceLineItems.apply(lineItems);
    }

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
        LocalDate birthday,
        Gender gender) {

        var id = UUID.randomUUID().toString();
        var token = RandomStringUtils.randomAlphanumeric(8);
        var confirmationToken = RandomStringUtils.randomAlphanumeric(8);

        return new Student(
            id, schoolTrip, schoolClass, firstName, lastName, birthday, gender,
            RegistrationState.CREATED, token, confirmationToken, null, null,
            new ArrayList<>(), null
        );
    }

    /**
     * Registers a mad payment for the student.
     *
     * @param payment  The payment which has been made.
     * @param students The repository to persist the changes.
     */
    public void addPayment(
        User executor, Payment payment, StudentsRepository students, SchoolTripsReadRepository schoolTrips
    ) {
        /*
         * Validate if creator is allowed.
         */
        var schoolTrip = schoolTrips.getSchoolTripById(this.schoolTrip);

        executor.checkPermission(
            DomainPermissions.ManageSchoolTrips.apply(),
            DomainPermissions.ManageSchoolTrip.apply(schoolTrip.getId())
        );

        /*
         * Avoid duplicate payments (idempotence)
         */
        this.payments = new ArrayList<>(
            this.payments.stream().filter(p -> !p.equals(payment)).toList()
        );

        /*
         * Make changes.
         */
        this.payments.add(payment);
        students.insertOrUpdateStudent(this);
    }

    /**
     * Assigns a student id for a school trip.
     *
     * @param id       The assigned id.
     * @param students The repository to read/ write the information.
     */
    public void assignSchoolTripStudentId(Integer id, StudentsRepository students) {
        this.tripStudentId = id;
        students.insertOrUpdateStudent(this);
    }

    public void completeOrUpdateStudentRegistrationByOrganizer(
        Questionnaire questionnaire,
        StudentsRepository students
    ) {
        this.questionnaire = questionnaire;
        this.registrationState = RegistrationState.REGISTERED;
        students.insertOrUpdateStudent(this);
    }

    public void completeStudentRegistration(
        Questionnaire questionnaire,
        String notificationEmail,
        StudentsRepository students,
        SchoolTripsReadRepository schoolTrips,
        JavaMailSender mailSender,
        SchoolTripApplicationConfiguration config,
        SchoolTripMessages messages) {

        boolean isUpdate = this.registrationState.equals(
            RegistrationState.REGISTERED
        );

        var trip = schoolTrips.getSchoolTripById(this.schoolTrip);

        this.questionnaire = questionnaire;
        this.registrationState = RegistrationState.WAITING_FOR_CONFIRMATION;
        this.notificationEmail = notificationEmail;

        students.insertOrUpdateStudent(this);

        // Send E-Mail for confirmation.
        var message = mailSender.createMimeMessage();
        var mimeHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

        var updateUrl = String
            .format(
                "%s/students/registered/%s", config.getUi().getBaseUrl(), confirmationToken
            )
            .replaceAll("([^:])//", "$1/");

        String mailText;

        if (isUpdate) {
            mailText = messages.registrationUpdatedMailText(
                messages, trip, this, updateUrl
            );
        } else {
            var confirmationUrl = String
                .format(
                    "%s/students/confirm-registration/%s", config.getUi().getBaseUrl(), confirmationToken
                )
                .replaceAll("([^:])//", "$1/");

            mailText = messages.registrationConfirmationMailText(
                messages, trip, this, confirmationUrl, updateUrl
            );
        }

        Operators.suppressExceptions(() -> {
            mimeHelper.setFrom(config.getEmail().getUsername());
            mimeHelper.setBcc(config.getEmail().getUsername());
            mimeHelper.setTo(notificationEmail);
            mimeHelper.setSubject(messages.confirmationMailSubject(trip));
            mimeHelper.setText(mailText);
        });

        mailSender.send(message);
    }

    public void confirmStudentRegistration(StudentsRepository students) {
        this.registrationState = RegistrationState.REGISTERED;
        this.token = RandomStringUtils.randomAlphanumeric(8);
        students.insertOrUpdateStudent(this);
    }

    @JsonIgnore
    public String getDisplayName() {
        return String.format("%s %s", firstName, lastName);
    }

    @Override
    public String getId() {
        return id;
    }

    public Payments getPayments() {
        return Payments.apply(payments);
    }

    public Optional<PriceLineItems> getPriceLineItems(
        Either<SchoolTripsReadRepository, SchoolTrip> schoolTrips,
        SchoolTripMessages i18n
    ) {
        return getQuestionnaire().map(q -> {
            var schoolTrip = schoolTrips
                .map(
                    l -> l.getSchoolTripById(this.schoolTrip.schoolTripId()),
                    r -> r
                );

            return Student.calculatePriceLineItems(schoolTrip, q, i18n);
        });
    }

    public Optional<Questionnaire> getQuestionnaire() {
        return Optional.ofNullable(questionnaire);
    }

    /**
     * Returns the unique id of the student which have been assigned
     * by the school trip tp the student.
     *
     * @return The id, if set.
     */
    public Optional<Integer> getSchoolTripStudentId() {
        return Optional.ofNullable(tripStudentId);
    }

    /**
     * Initially creates (persists) this student class.
     *
     * @param creator    The user who wants to create the school trip.
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
            DomainPermissions.ManageSchoolTrips.apply(),
            DomainPermissions.ManageSchoolTrip.apply(schoolTrip.getId())
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

    /**
     * Remove an existing payment.
     *
     * @param id The id of the payment.
     */
    public void removePayment(
        User executor, String id, StudentsRepository studens, SchoolTripsReadRepository schoolTrips) {

        /*
         * Validate if creator is allowed.
         */
        var schoolTrip = schoolTrips.getSchoolTripById(this.schoolTrip);

        executor.checkPermission(
            DomainPermissions.ManageSchoolTrips.apply(),
            DomainPermissions.ManageSchoolTrip.apply(schoolTrip.getId())
        );

        /*
         * Make Changes.
         */
        this.payments = new ArrayList<>(
            this.payments.stream().filter(p -> !p.getId().equals(id)).toList()
        );
    }

    /**
     * Should be called, when student has been removed from a school trip.
     *
     * @param students The repository to read/ write student information.
     */
    public void removeStudentFromSchoolTrip(StudentsRepository students) {
        // As of now we delete students compeltely.
        students.remove(this);
    }

    public void updateStudentProperties(
        String schoolClass, String firstName, String lastName, LocalDate birthday, Gender gender,
        StudentsRepository students, SchoolTripsReadRepository schoolTrips
    ) {
        /*
         * Validation
         */
        // Will throw an exception if school class not found.
        var schoolTrip = schoolTrips.getSchoolTripById(this.schoolTrip);
        schoolTrip.getSchoolClassByName(schoolClass);

        /*
         * Update properties.
         */
        var schoolClassBefore = this.schoolClass;

        this.schoolClass = schoolClass;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.gender = gender;

        /*
         * Events
         */
        if (!schoolClassBefore.equals(schoolClass)) {
            this.registerEvent(StudentsSchoolClassChangedEvent.apply(schoolClassBefore, this));
        }

        students.insertOrUpdateStudent(this);
    }

}
