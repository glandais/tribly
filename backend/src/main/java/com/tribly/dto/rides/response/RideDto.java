package com.tribly.dto.rides.response;

import com.tribly.domain.ride.Ride;
import com.tribly.dto.publications.response.PublicationDto;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import java.time.Instant;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

// Response DTOs
@Schema(description = "Ride summary data", allOf = PublicationDto.class)
public record RideDto(
    @Schema(description = "Ride ID (TSID)", required = true) String id,
    @Schema(description = "Ride URL slug", required = true) String slug,
    @Schema(description = "Ride name", required = true) String name,
    @Nullable @Schema(description = "Ride description") String description,
    @Schema(description = "Ride date/time", required = true) Instant dateTime,
    @Schema(description = "Ride status", required = true) Status status,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Nullable @Schema(description = "Route slug") String routeSlug,
    @Schema(description = "Number of participants", required = true) int participantCount,
    @Schema(description = "Number of groups", required = true) int groupCount,
    @Schema(description = "Ride groups", required = true) List<RideGroupDto> groups,
    @Nullable @Schema(description = "Publication timestamp") Instant publishAt,
    @Nullable @Schema(description = "Creation timestamp") Instant createdAt)
    implements PublicationDto {
  public static RideDto from(Ride ride, boolean groupDetails) {
    List<RideGroupDto> groupDtos =
        groupDetails
            ? ride.getGroups().stream().filter(g -> !g.isDeleted()).map(RideGroupDto::from).toList()
            : List.of();
    return new RideDto(
        TsidUtils.toString(ride.getId()),
        ride.getSlug(),
        ride.getName(),
        ride.getDescription(),
        ride.getDateTime(),
        ride.getStatus(),
        ride.getVisibility(),
        ride.getRoute() != null ? ride.getRoute().getSlug() : null,
        ride.getParticipantCount(),
        ride.getGroupCount(),
        groupDtos,
        ride.getPublishAt(),
        ride.getCreatedAt());
  }
}
