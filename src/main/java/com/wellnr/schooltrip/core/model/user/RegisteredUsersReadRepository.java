package com.wellnr.schooltrip.core.model.user;

import com.wellnr.ddd.DomainRepository;

import java.util.List;
import java.util.Optional;

public interface RegisteredUsersReadRepository extends DomainRepository {

    List<RegisteredUser> findAll();

    Optional<RegisteredUser> findOneByEmail(String email);

    default RegisteredUser getOneByEmail(String email) {
        return findOneByEmail(email).orElseThrow();
    }

}
