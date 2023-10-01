package com.wellnr.schooltrip.core.application.commands.schooltrip;

import com.wellnr.ddd.commands.MessageResult;
import com.wellnr.schooltrip.core.SchoolTripDomainRegistry;
import com.wellnr.schooltrip.core.application.commands.AbstractSchoolTripCommand;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTrip;
import com.wellnr.schooltrip.core.model.schooltrip.SchoolTripSettings;
import com.wellnr.schooltrip.core.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class UpdateSchoolTripSettingsCommand implements AbstractSchoolTripCommand<MessageResult<SchoolTrip>> {

    final String name;
    double basePrice;
    double skiRentalPrice;
    double skiBootsRentalPrice;
    double snowboardRentalPrice;
    double snowboardBootsRentalPrice;
    double helmetRentalPrice;
    double tShirtPrice;

    String initialPaymentUrl;
    String remainingPaymentUrl;
    String completePaymentUrl;

    LocalDateTime registrationOpenUntil;

    public static UpdateSchoolTripSettingsCommand apply(String name, SchoolTripSettings settings) {
        return new UpdateSchoolTripSettingsCommand(
            name,
            settings.getBasePrice(),
            settings.getSkiRentalPrice(),
            settings.getSkiBootsRentalPrice(),
            settings.getSnowboardRentalPrice(),
            settings.getSnowboardBootsRentalPrice(),
            settings.getHelmetRentalPrice(),
            settings.getTShirtPrice(),
            settings.getInitialPaymentUrl(),
            settings.getRemainingPaymentUrl(),
            settings.getCompletePaymentUrl(),
            settings.getRegistrationOpenUntil()
        );
    }

    @Override
    public MessageResult<SchoolTrip> run(User user, SchoolTripDomainRegistry domainRegistry) {
        var trip = domainRegistry.getSchoolTrips().getSchoolTripByName(name);

        trip.updateSettings(
            SchoolTripSettings.apply(
                basePrice, skiRentalPrice, skiBootsRentalPrice,
                snowboardRentalPrice, snowboardBootsRentalPrice, helmetRentalPrice, tShirtPrice,
                initialPaymentUrl, remainingPaymentUrl, completePaymentUrl,
                registrationOpenUntil
            ),
            domainRegistry.getSchoolTrips()
        );

        return MessageResult
            .apply(user.getMessages().successfullyUpdatedTrip(name))
            .withData(trip);
    }

}
