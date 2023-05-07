package com.wellnr.schooltrip.core.application;

import com.wellnr.ddd.events.DomainServices;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.schooltrip.events.SchoolTripCreatedEvent;
import com.wellnr.schooltrip.core.model.student.StudentId;
import com.wellnr.schooltrip.core.model.student.student.StudentRegisteredEvent;
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


}
