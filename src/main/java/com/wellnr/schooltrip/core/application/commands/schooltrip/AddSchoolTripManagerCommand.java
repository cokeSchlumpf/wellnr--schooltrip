package com.wellnr.schooltrip.core.application.commands.schooltrip;

import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripId;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.payments.Payment;
import com.wellnr.schooltrip.core.model.student.payments.PaymentType;
import com.wellnr.schooltrip.core.model.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class AddSchoolTripManagerCommand implements AbstractSchoolTripCommand<MessageResult<Nothing>> {

    SchoolTripId schoolTrip;

    @NotBlank
    @Email
    String email;

    public static AddSchoolTripManagerCommand apply(SchoolTrip schoolTrip) {
        return apply(new SchoolTripId(schoolTrip.getId()), "");
    }

    @Override
    public MessageResult<Nothing> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var trip = domainRegistry
            .getSchoolTrips()
            .getSchoolTripById(schoolTrip);

        var result = trip.addManager(
            user, email, domainRegistry.getSchoolTrips(), domainRegistry.getUsers()
        );

        return MessageResult.formatted(
            "Added `%s` as manager for trip.", result.getName()
        );
    }

}
