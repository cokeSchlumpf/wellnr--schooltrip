package com.wellnr.schooltrip.infrastructure.repositories;

import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.schooltrip.repository.SchoolTripsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
@AllArgsConstructor
public class SchoolTripsMongoRepository implements SchoolTripsRepository {

    private final SchoolTripsSpringDataMongoRepository spring;

    @Override
    public Collection<SchoolTrip> findAllSchoolTrips() {
        return spring.findAll();
    }

    @Override
    public Optional<SchoolTrip> findSchoolTripById(String id) {
        return spring.findById(id);
    }

    @Override
    public Optional<SchoolTrip> findSchoolTripByName(String name) {
        return spring.findOneByName(name);
    }

    @Override
    public void save(SchoolTrip schoolTrip) {
        spring.save(schoolTrip);
    }

}
