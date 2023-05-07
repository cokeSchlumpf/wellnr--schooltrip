package com.wellnr.schooltrip.core.model.schooltrip.repository;

import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;

import java.util.Collection;
import java.util.Optional;

public interface SchoolTripsReadRepository {

    /*
     * SchoolTrips
     */
    Optional<SchoolTrip> findSchoolTripByName(String name);

    Optional<SchoolTrip> findSchoolTripById(String id);

    default SchoolTrip getSchoolTripById(String id) {
        return findSchoolTripById(id).orElseThrow(
            () -> new RuntimeException("SchoolTripNotFound: " + id)
        );
    }

    default SchoolTrip getSchoolTripById(SchoolTripId id) {
        return getSchoolTripById(id.schoolTripId());
    }

    default SchoolTrip getSchoolTripByName(String name) {
        return findSchoolTripByName(name).orElseThrow();
    }

    Collection<SchoolTrip> findAllSchoolTrips();

}
