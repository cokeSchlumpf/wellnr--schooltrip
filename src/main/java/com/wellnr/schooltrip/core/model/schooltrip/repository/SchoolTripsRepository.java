package com.wellnr.schooltrip.core.model.schooltrip.repository;

import com.wellnr.ddd.DomainRepository;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;

public interface SchoolTripsRepository extends SchoolTripsReadRepository, DomainRepository {

    void save(SchoolTrip schoolTrip);

}
