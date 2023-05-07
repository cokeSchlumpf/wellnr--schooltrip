package com.wellnr.schooltrip.infrastructure.repositories;

import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RegisteredUsersSpringDataMongoRepository extends MongoRepository<RegisteredUser, String> {

    Optional<RegisteredUser> findOneByEmail(String email);

}
