package com.wellnr.schooltrip.core.application.commands.schooltrip;

import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.student.Student;
import com.wellnr.schooltrip.core.model.student.payments.Payment;
import com.wellnr.schooltrip.core.model.student.payments.PaymentType;
import com.wellnr.schooltrip.core.model.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    @NotBlank
    String description;

    double amount;

    public static AddPaymentCommand apply(Student student) {
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
            user.getMessages().successfullyAddedPaymentForUser(student)
        );
    }

    public AddPaymentCommand withStudentId(Student student) {
        return new AddPaymentCommand(student.getId(), date, type, description, amount);
    }

}
