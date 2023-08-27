package com.wellnr.schooltrip.core.model.schooltrip.events;

import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * This event is fired when a school trip has been created.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class SchoolTripCreatedEvent {

    SchoolTrip trip;

}
