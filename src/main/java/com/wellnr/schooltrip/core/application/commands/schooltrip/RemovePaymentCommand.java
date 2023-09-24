package com.wellnr.schooltrip.core.application.commands.schooltrip;

import com.wellnr.common.markup.Nothing;
import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

        return MessageResult.apply(
            user.getMessages().successfullyRemovedPayment(student)
        );
    }

}
