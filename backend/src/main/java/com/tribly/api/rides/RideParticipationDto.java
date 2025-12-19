package com.tribly.api.rides;

import com.tribly.domain.ride.RideParticipation;
import com.tribly.infrastructure.id.TsidUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride participation information")
public record RideParticipationDto(
    @Schema(description = "Participation ID (TSID)", required = true) String id,
    @Schema(description = "User ID (TSID)", required = true) String userId,
    @Schema(description = "Participation status", required = true) String status,
    @Nullable @Schema(description = "Registration timestamp", nullable = true) String registeredAt,
    @Nullable @Schema(description = "Participant notes", nullable = true) String notes) {
  public static RideParticipationDto from(RideParticipation participation) {
    return new RideParticipationDto(
        TsidUtils.toString(participation.getId()),
        TsidUtils.toString(participation.getUser().getId()),
        participation.getStatus().name(),
        participation.getRegisteredAt().toString(),
        participation.getNotes());
  }
}
