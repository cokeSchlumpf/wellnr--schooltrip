package com.wellnr.schooltrip.infrastructure;

import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.SchoolTripApplicationConfiguration;
import com.wellnr.schooltrip.core.model.user.RegisteredUser;
import com.wellnr.schooltrip.core.model.user.rbac.DomainRoles;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@AllArgsConstructor
public class ApplicationStartupTasks {

    private SchoolTripApplicationConfiguration config;

    private SchoolTripDomainRegistry registry;

    @PostConstruct
    public void initializeDefaultAdminUsers() {
        config.getDefaultAdminUsers().forEach(defaultUser -> {
            log.info("Checking default user `{}`", defaultUser.getEmail());

            var maybeUser = registry.getUsers().findOneByEmail(defaultUser.getEmail());

            if (maybeUser.isEmpty()) {
                log.info("Register default user `{}`.", defaultUser.getEmail());

                RegisteredUser
                    .createNew(
                        defaultUser.getEmail(),
                        defaultUser.getPassword(),
                        defaultUser.getFirstName(),
                        defaultUser.getLastName(),
                        Set.of(DomainRoles.ApplicationAdministrator.apply()),
                        registry.getPasswordEncryptionPort()
                    )
                    .register(registry.getUsers());
            }
        });
    }

}
