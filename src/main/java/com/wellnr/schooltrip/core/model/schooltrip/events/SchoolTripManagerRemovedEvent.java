package com.wellnr.schooltrip.core.model.schooltrip.events;

import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import com.wellnr.schooltrip.core.model.user.RegisteredUserId;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * This event is fired when a school trip has been created.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class SchoolTripManagerRemovedEvent {

    SchoolTrip trip;

    RegisteredUserId user;

}
