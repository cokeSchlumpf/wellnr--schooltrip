package com.wellnr.schooltrip.core.model.schooltrip.events;

import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolClass;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * This event is fired when a new school class has been registered within a school trip.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class SchoolClassRegisteredEvent {

    SchoolTripId schoolTripId;

    SchoolClass schoolClass;

}
