package com.wellnr.schooltrip.core.application.commands;

import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.payments.Payment;
import com.wellnr.schooltrip.core.model.student.payments.PaymentType;
import com.wellnr.schooltrip.core.model.user.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RemovePaymentCommand implements AbstractSchoolTripCommand<MessageResult<Nothing>> {

    private final String studentId;

    private final String paymentId;

    @Override
    public MessageResult<Nothing> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var student = domainRegistry.getStudents().getStudentById(this.studentId);

        student.removePayment(
            user, paymentId, domainRegistry.getStudents(), domainRegistry.getSchoolTrips()
        );

        return MessageResult.formatted(
            "Successfully removed payment from `%s`", student.getDisplayName()
        );
    }

}
