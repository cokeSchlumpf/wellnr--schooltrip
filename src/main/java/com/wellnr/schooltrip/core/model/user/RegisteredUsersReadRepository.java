package com.wellnr.schooltrip.core.model.user;

import com.wellnr.ddd.DomainRepository;

import java.util.List;
import java.util.Optional;

public interface RegisteredUsersReadRepository extends DomainRepository {

    List<RegisteredUser> findAll();

    Optional<RegisteredUser> findOneById(String id);

    Optional<RegisteredUser> findOneByEmail(String email);

    default RegisteredUser getOneById(String id) {
        return findOneById(id).orElseThrow(); // TODO: Better exception.
    }

    default RegisteredUser getOneByEmail(String email) {
        return findOneByEmail(email).orElseThrow(); // TODO: Better exception.
    }

}
