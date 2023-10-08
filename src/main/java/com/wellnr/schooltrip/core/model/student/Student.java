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
import com.wellnr.schooltrip.core.model.student.exceptions.StudentAlreadyRegisteredException;
import com.wellnr.schooltrip.core.model.student.payments.Payment;
import com.wellnr.schooltrip.core.model.student.payments.Payments;
import com.wellnr.schooltrip.core.model.student.payments.PriceLineItem;
import com.wellnr.schooltrip.core.model.student.payments.PriceLineItems;
import com.wellnr.schooltrip.core.model.student.questionaire.Questionnaire;
import com.wellnr.schooltrip.core.model.student.questionaire.Ski;
import com.wellnr.schooltrip.core.model.student.questionaire.Snowboard;
import com.wellnr.schooltrip.core.model.student.questionaire.TShirtSelection;
import com.wellnr.schooltrip.core.model.user.User;
import com.wellnr.schooltrip.core.model.user.rbac.DomainPermissions;
import com.wellnr.schooltrip.core.ports.i18n.SchoolTripMessages;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Student extends AggregateRoot<String, Student> {

    public static final double INITIAL_PAYMENT_AMOUNT = 160.00;

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

    String paymentToken;

    Questionnaire questionnaire;

    String notificationEmail;

    List<Payment> payments;

    Integer tripStudentId;

    RejectionReason rejectionReason;

    public static PriceLineItems calculatePriceLineItems(
        SchoolTrip schoolTrip, Questionnaire questionnaire, SchoolTripMessages i18n
    ) {
        var lineItems = new ArrayList<PriceLineItem>();

        lineItems.add(new PriceLineItem(
            i18n.basePrice(), schoolTrip.getSettings().getBasePrice(), false
        ));

        if (questionnaire.getDisziplin() instanceof Ski ski) {
            if (ski.getRental().isPresent() && ski.getBootRental().isPresent()) {
                lineItems.add(new PriceLineItem(
                    i18n.skiAndBootRental(), schoolTrip.getSettings().getSkiAndBootsRentalPrice(), true
                ));
            } else {
                if (ski.getRental().isPresent()) {
                    lineItems.add(new PriceLineItem(
                        i18n.skiRental(), schoolTrip.getSettings().getSkiRentalPrice(), true
                    ));
                }

                if (ski.getBootRental().isPresent()) {
                    lineItems.add(new PriceLineItem(
                        i18n.skiBootRental(), schoolTrip.getSettings().getSkiBootsRentalPrice(), true
                    ));
                }
            }
        }

        if (questionnaire.getDisziplin() instanceof Snowboard sb) {
            if (sb.getRental().isPresent() && sb.getBootRental().isPresent()) {
                lineItems.add(new PriceLineItem(
                    i18n.snowboardAndBootRental(), schoolTrip.getSettings().getSnowboardAndBootsRentalPrice(), true
                ));
            } else {
                if (sb.getRental().isPresent()) {
                    lineItems.add(new PriceLineItem(
                        i18n.snowboardRental(), schoolTrip.getSettings().getSnowboardRentalPrice(), true
                    ));
                }

                if (sb.getBootRental().isPresent()) {
                    lineItems.add(new PriceLineItem(
                        i18n.snowboardBootRental(), schoolTrip.getSettings().getSnowboardBootsRentalPrice(), true
                    ));
                }
            }
        }

        if (questionnaire.getDisziplin().hasHelmRental()) {
            lineItems.add(new PriceLineItem(
                i18n.helmetRental(), schoolTrip.getSettings().getHelmetRentalPrice(), true
            ));
        }

        if (!questionnaire.getTShirtSelection().equals(TShirtSelection.NONE)) {
            lineItems.add(new PriceLineItem(
                i18n.tripTShirt(), schoolTrip.getSettings().getTShirtPrice(), true
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
        var paymentToken = RandomStringUtils.randomAlphanumeric(8);

        return new Student(
            id, schoolTrip, schoolClass, firstName, lastName, birthday, gender,
            RegistrationState.CREATED, token, confirmationToken, paymentToken, null, null,
            new ArrayList<>(), null, null
        );
    }

    /**
     * Registers a mad payment for the student.
     *
     * @param executor    The user who entered the payment.
     * @param payment     The payment which has been made.
     * @param students    The repository to persist the changes.
     * @param schoolTrips The repository to read/ write the information.
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

        receivedPayment(payment, students);
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

        var toBePaid = this
            .getPriceLineItems(Either.fromRight(trip), messages)
            .map(PriceLineItems::getAmountPaymentsBeforeTrip)
            .orElse(0.0);

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
                messages, trip, this,
                INITIAL_PAYMENT_AMOUNT, toBePaid - INITIAL_PAYMENT_AMOUNT,
                confirmationUrl, updateUrl, getPaymentLinks(schoolTrips, messages)
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
        this.token = RandomStringUtils.randomAlphanumeric(32);
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

    public Optional<String> getNotificationEmail() {
        return Optional.ofNullable(notificationEmail);
    }

    public String getInitialPaymentUrl(SchoolTrip schoolTrip) {
        return schoolTrip.getSettings().getInitialPaymentUrl().replace(":paymentToken", this.paymentToken);
    }

    public String getRemainingPaymentUrl(SchoolTrip schoolTrip) {
        return schoolTrip.getSettings().getRemainingPaymentUrl().replace(":paymentToken", this.paymentToken);
    }

    public String getCompletePaymentUrl(SchoolTrip schoolTrip) {
        return schoolTrip.getSettings().getCompletePaymentUrl().replace(":paymentToken", this.paymentToken);
    }

    public Map<String, String> getPaymentLinks(SchoolTripsReadRepository schoolTrips, SchoolTripMessages i18n) {
        var schoolTrip = schoolTrips.getSchoolTripById(this.schoolTrip.schoolTripId());

        var links = new HashMap<String, String>();

        links.put(
            i18n.initialPayment(),
            getInitialPaymentUrl(schoolTrip)
        );

        links.put(
            i18n.remainingPayment(),
            getRemainingPaymentUrl(schoolTrip)
        );

        links.put(
            i18n.completePayment(),
            getCompletePaymentUrl(schoolTrip)
        );

        return Map.copyOf(links);
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

    public Optional<RejectionReason> getRejectionReason() {
        return Optional.ofNullable(rejectionReason);
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
     * Registers a mad payment for the student (received from a system, not entered manually).
     *
     * @param payment  The payment which has been made.
     * @param students The repository to persist the changes.
     */
    public void receivedPayment(
        Payment payment, StudentsRepository students
    ) {
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
         * Ensure that tokens are unique.
         */


        /*
         * Actual logic:
         * - Check whether trip already exists (from a different entity).
         * - If it does not exist, create.
         * - If it exists for the current entity, do nothing.
         */
        if (existing.isPresent() && !existing.get().id.equals(this.id)) {
            throw StudentAlreadyExistsException.apply();
        } else if (existing.isEmpty()) {
            /*
             * Ensure tokens are not duplicated.
             */
            var duplicatedTokens = true;
            do {
                this.token = RandomStringUtils.randomAlphanumeric(8);
                this.confirmationToken = RandomStringUtils.randomAlphanumeric(8);
                this.paymentToken = RandomStringUtils.randomAlphanumeric(8);

                var maybExistingToken = repository.findStudentByToken(this.token);
                var maybExistingConfirmationToken = repository.findStudentByConfirmationToken(this.confirmationToken);
                var maybExistingPaymentToken = repository.findStudentByPaymentToken(this.paymentToken);

                duplicatedTokens =
                    maybExistingToken.isPresent() && maybExistingConfirmationToken.isPresent() && maybExistingPaymentToken.isPresent();
            } while (duplicatedTokens);

            /*
             * Save student.
             */
            this.registerEvent(StudentRegisteredEvent.apply(this));
            repository.insertOrUpdateStudent(this);
        }
    }

    public void rejectParticipation(
        User executor, RejectionReason rejectionReason, StudentsRepository students,
        SchoolTripsReadRepository schoolTrips, JavaMailSender mailSender, SchoolTripMessages i18n,
        SchoolTripApplicationConfiguration config
    ) {
        /*
         * Check pre-conditions.
         *
         * Rejection is only possible, if student is not already registered or the user
         * updating the student is a manager of the school trip or an administrator.
         */
        var isAdminUser = executor.hasPermission(
            DomainPermissions.ManageSchoolTrips.apply(),
            DomainPermissions.ManageSchoolTrip.apply(this.schoolTrip.schoolTripId())
        );

        if (registrationState.equals(RegistrationState.REGISTERED) && !isAdminUser) {
            throw StudentAlreadyRegisteredException.apply(this);
        }

        var schoolTrip = schoolTrips.getSchoolTripById(this.schoolTrip.schoolTripId());

        /*
         * Execute action.
         */
        this.registrationState = RegistrationState.REJECTED;
        this.rejectionReason = rejectionReason;
        this.questionnaire = null;

        // Send E-Mail for confirmation.
        var message = mailSender.createMimeMessage();
        var mimeHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

        Operators.suppressExceptions(() -> {
            mimeHelper.setFrom(config.getEmail().getUsername());
            mimeHelper.setTo(config.getEmail().getUsername());
            mimeHelper.setSubject(
                i18n.confirmRejectionMailSubject(schoolTrip)
            );
            mimeHelper.setText(
                i18n.confirmRejectionMailText(schoolTrip, this, rejectionReason)
            );
        });

        mailSender.send(message);

        students.insertOrUpdateStudent(this);
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

    public void resetRejection(User users, StudentsRepository students) {
        /*
         * User needs to be administrator.
         */
        users.checkPermission(
            DomainPermissions.ManageSchoolTrips.apply(),
            DomainPermissions.ManageSchoolTrip.apply(this.schoolTrip.schoolTripId())
        );

        this.rejectionReason = null;
        this.registrationState = RegistrationState.CREATED;
        students.insertOrUpdateStudent(this);
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
