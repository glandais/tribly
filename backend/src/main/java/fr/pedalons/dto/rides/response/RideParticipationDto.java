package fr.pedalons.dto.rides.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ride.RideParticipation;
import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride participation information")
@ValidateSchema
public record RideParticipationDto(
    @Schema(description = "Participation ID (TSID)", required = true) String id,
    @Schema(description = "User ID (TSID)", required = true) String userId,
    @Nullable @Schema(description = "Registration timestamp") Instant registeredAt) {
  public static RideParticipationDto from(RideParticipation participation) {
    return new RideParticipationDto(
        TsidUtils.toString(participation.getId()),
        TsidUtils.toString(participation.getUser().getId()),
        participation.getRegisteredAt());
  }
}
