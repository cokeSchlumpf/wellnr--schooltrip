package com.wellnr.schooltrip.core;

import com.wellnr.ddd.BeanValidation;
import com.wellnr.ddd.DomainRegistry;
import com.wellnr.schooltrip.core.model.schooltrip.repository.SchoolTripsRepository;
import com.wellnr.schooltrip.core.model.student.StudentsRepository;
import com.wellnr.schooltrip.core.model.user.RegisteredUsersRepository;
import com.wellnr.schooltrip.core.ports.PasswordEncryptionPort;
import com.wellnr.schooltrip.core.ports.SchoolTripMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.mail.javamail.JavaMailSender;

@Getter
@AllArgsConstructor(staticName = "apply")
public class SchoolTripDomainRegistry implements DomainRegistry {

    BeanValidation validation;

    SchoolTripsRepository schoolTrips;

    StudentsRepository students;

    RegisteredUsersRepository users;

    PasswordEncryptionPort passwordEncryptionPort;

    SchoolTripMessages messages;

    JavaMailSender mailSender;

}
