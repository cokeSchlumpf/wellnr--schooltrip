package com.wellnr.schooltrip.infrastructure.repositories;

import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SchoolTripsSpringDataMongoRepository extends MongoRepository<SchoolTrip, String> {

    Optional<SchoolTrip> findOneByName(String name);

}
