package com.wellnr.schooltrip.core.model.schooltrip.repository;

import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.schooltrip.exceptions.SchoolTripNotFoundException;

import java.util.Collection;
import java.util.Optional;

public interface SchoolTripsReadRepository {

    Collection<SchoolTrip> findAllSchoolTrips();

    Optional<SchoolTrip> findSchoolTripById(String id);

    /*
     * SchoolTrips
     */
    Optional<SchoolTrip> findSchoolTripByName(String name);

    default SchoolTrip getSchoolTripById(SchoolTripId id) {
        return getSchoolTripById(id.schoolTripId());
    }

    default SchoolTrip getSchoolTripById(String id) {
        return findSchoolTripById(id).orElseThrow(
            () -> new RuntimeException("SchoolTripNotFound: " + id)
        );
    }

    default SchoolTrip getSchoolTripByName(String name) {
        return findSchoolTripByName(name).orElseThrow(
            () -> SchoolTripNotFoundException.withName(name)
        );
    }

}
