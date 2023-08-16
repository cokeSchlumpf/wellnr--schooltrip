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
public class AddPaymentCommand implements AbstractSchoolTripCommand<MessageResult<Nothing>> {

    private final String studentId;

    @NotNull
    LocalDate date;

    @NotNull
    PaymentType type;

    @NotNull
    String description;

    @Min(0)
    double amount;

    public static  AddPaymentCommand apply(Student student) {
        return apply(student.getId(), LocalDate.now(), PaymentType.TRANSACTION, null, 0);
    }

    @Override
    public MessageResult<Nothing> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var payment = Payment.createNew(date, type, description, amount);
        var student = domainRegistry.getStudents().getStudentById(this.studentId);

        student
            .addPayment(
                user, payment, domainRegistry.getStudents(), domainRegistry.getSchoolTrips()
            );

        return MessageResult.formatted(
            "Successfully added payment for `%s`", student.getDisplayName()
        );
    }

}
