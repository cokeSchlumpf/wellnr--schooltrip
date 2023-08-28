package com.wellnr.schooltrip.core.model.user.rbac;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DomainRoles {

    public static final DomainRole APP_ADMINISTRATOR = DomainRole.apply(
        "/app/admin",
        List.of(
            DomainPermissions.APPLICATION__MANAGE_TRIPS
        )
    );

    public static final DomainRole TEACHER = DomainRole.apply(
        "/app/trips/teacher",
        List.of(
            DomainPermissions.TRIPS__MANAGE_TRIP
        )
    );

    public static final DomainRole STUDENT = DomainRole.apply(
        "/app/trips/student",
        List.of(
            DomainPermissions.STUDENTS__EDIT_STUDENT
        )
    );

    private static final Map<String, DomainRole> domainRoles;

    static {
        domainRoles = Stream
            .of(APP_ADMINISTRATOR, TEACHER, STUDENT)
            .collect(Collectors.toMap(
                DomainRole::getName,
                r -> r
            ));
    }

    private DomainRoles() {

    }

    public static DomainRole getByName(String name) {
        if (!domainRoles.containsKey(name)) {
            throw new RuntimeException(MessageFormat.format(
                "Role `{0}` does not exist.",
                name
            ));
        }

        return domainRoles.get(name);
    }

}
