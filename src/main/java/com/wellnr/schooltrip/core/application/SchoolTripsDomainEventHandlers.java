package com.wellnr.schooltrip.core.application;

import com.wellnr.ddd.events.DomainServices;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.schooltrip.events.*;
import com.wellnr.schooltrip.core.model.stripe.PaymentReceivedEvent;
import com.wellnr.schooltrip.core.model.student.StudentId;
import com.wellnr.schooltrip.core.model.student.events.StudentRegisteredEvent;
import com.wellnr.schooltrip.core.model.student.events.StudentsSchoolClassChangedEvent;
import com.wellnr.schooltrip.core.model.student.payments.Payment;
import com.wellnr.schooltrip.core.model.student.payments.PaymentType;
import com.wellnr.schooltrip.core.model.user.rbac.DomainRoles;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@DomainServices
@AllArgsConstructor(staticName = "apply")
public class SchoolTripsDomainEventHandlers {

    private final SchoolTripDomainRegistry domainRegistry;

    @Async
    @EventListener
    public void handle(SchoolTripCreatedEvent schoolTripCreatedEvent) {

    }

    @Async
    @EventListener
    public void onSchoolTripManagerAdded(SchoolTripManagerAddedEvent event) {
        domainRegistry
            .getUsers()
            .getOneById(event.getUser().id())
            .grantDomainRole(
                DomainRoles.SchoolTripManager.apply(event.getTrip().getId()),
                domainRegistry.getUsers()
            );
    }

    @Async
    @EventListener
    public void onSchoolTripManagerRemoved(SchoolTripManagerRemovedEvent event) {
        domainRegistry
            .getUsers()
            .getOneById(event.getUser().id())
            .revokeDomainRole(
                DomainRoles.SchoolTripManager.apply(event.getTrip().getId()),
                domainRegistry.getUsers()
            );
    }

    @Async
    @EventListener
    public void onStudentIDAssigned(StudentIDAssigndEvent event) {
        domainRegistry
            .getStudents()
            .getStudentById(event.getStudent().id())
            .assignSchoolTripStudentId(event.getId(), domainRegistry.getStudents());
    }

    @Async
    @EventListener
    public void onStudentRegistered(StudentRegisteredEvent event) {
        domainRegistry
            .getSchoolTrips()
            .getSchoolTripById(event.getStudent().getSchoolTrip())
            .registerStudent(
                event.getStudent().getSchoolClass(),
                new StudentId(event.getStudent().getId()),
                domainRegistry.getSchoolTrips()
            );
    }

    @Async
    @EventListener
    public void onStudentRemovedFromSchoolTripEvent(StudentRemovedFromSchoolTripEvent event) {
        domainRegistry
            .getStudents()
            .getStudentById(event.getStudent().id())
            .removeStudentFromSchoolTrip(domainRegistry.getStudents());
    }

    @Async
    @EventListener
    public void onStudentsSchoolClassChanged(StudentsSchoolClassChangedEvent event) {
        domainRegistry
            .getSchoolTrips()
            .getSchoolTripById(event.getStudent().getSchoolTrip())
            .updateStudentsClass(
                event.getOldClass(),
                event.getStudent().getSchoolClass(),
                new StudentId(event.getStudent().getId()),
                domainRegistry.getSchoolTrips()
            );
    }

    @Async
    @EventListener
    public void onPaymentReceivedEvent(PaymentReceivedEvent event) {
        domainRegistry
            .getStudents()
            .findStudentByPaymentToken(event.getPaymentToken())
            .ifPresent(student -> {
                var payment = Payment.createNew(
                    event.getCreated(), PaymentType.ONLINE, "Online payment.", event.getAmount()
                );

                student.receivedPayment(
                    payment, domainRegistry.getStudents()
                );
            });

    }
}
