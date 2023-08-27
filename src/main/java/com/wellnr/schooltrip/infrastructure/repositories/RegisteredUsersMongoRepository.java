package com.wellnr.schooltrip.infrastructure.repositories;

import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import com.wellnr.schooltrip.core.model.user.RegisteredUsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class RegisteredUsersMongoRepository implements RegisteredUsersRepository {

    private final RegisteredUsersSpringDataMongoRepository spring;

    @Override
    public List<RegisteredUser> findAll() {
        return spring.findAll();
    }

    @Override
    public Optional<RegisteredUser> findOneByEmail(String email) {
        return spring.findOneByEmail(email);
    }

    @Override
    public void insertOrUpdate(RegisteredUser registeredUser) {
        spring.save(registeredUser);
    }

}
