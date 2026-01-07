package com.tribly.dto.trips.response;

import com.tribly.common.TsidUtils;
import com.tribly.domain.trip.TripParticipation;
import com.tribly.dto.validation.ValidateSchema;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Trip participation information")
@ValidateSchema
public record TripParticipationDto(
    @Schema(description = "Participation ID (TSID)", required = true) String id,
    @Schema(description = "User ID (TSID)", required = true) String userId,
    @Nullable @Schema(description = "Registration timestamp") Instant registeredAt) {

  public static TripParticipationDto from(TripParticipation participation) {
    return new TripParticipationDto(
        TsidUtils.toString(participation.getId()),
        TsidUtils.toString(participation.getUser().getId()),
        participation.getRegisteredAt());
  }
}
