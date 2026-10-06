package fr.pedalons.dto.routes.response;

import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * One place a route is used: a ride or a trip that references it, either directly or through a
 * child (a ride's group or a trip's stage). One entry per publication.
 */
@Schema(description = "A ride or trip that uses a route")
@ValidateSchema
public record RouteUsageDto(
    @Schema(description = "Publication type (RIDE or TRIP)", required = true) PublicationType type,
    @Schema(description = "Publication URL slug", required = true) String slug,
    @Schema(description = "Publication name", required = true) String name,
    @Schema(description = "Publication date/time", required = true) Instant dateTime,
    @Schema(
            description =
                "IANA zone of the publication, as RideDto.timezone / TripDto.timezone: dateTime"
                    + " and endDate are rendezvous in it.",
            examples = "Europe/Paris",
            required = true)
        String timezone,
    @Nullable
        @Schema(
            description =
                "For a trip, the date of its last stage — the same value as TripDto.endDate. Null"
                    + " for a ride, and for a trip with no stage, which lasts a day.")
        Instant endDate,
    @Schema(description = "Slug of the team owning the publication", required = true)
        String teamSlug,
    @Schema(
            description =
                "Whether the publication references the route directly (not only via a child)",
            required = true)
        boolean referencedDirectly,
    @Schema(
            description =
                "Names of the ride groups or trip stages that reference the route, if any",
            required = true)
        List<String> viaChildNames) {

  public static RouteUsageDto fromRide(Ride ride, Long routeId) {
    List<String> viaGroups =
        ride.getGroups().stream()
            .filter(group -> referencesRoute(group.getRoute(), routeId))
            .map(RideGroup::getName)
            .toList();
    return new RouteUsageDto(
        PublicationType.RIDE,
        ride.getSlug(),
        ride.getName(),
        ride.getDateTime(),
        ride.zone().getId(),
        null,
        ride.getTeam().getSlug(),
        referencesRoute(ride.getRoute(), routeId),
        viaGroups);
  }

  public static RouteUsageDto fromTrip(Trip trip, Long routeId) {
    List<TripStage> liveStages = trip.getStages().stream().filter(s -> !s.isDeleted()).toList();
    List<String> viaStages =
        liveStages.stream()
            .filter(stage -> referencesRoute(stage.getRoute(), routeId))
            .map(TripStage::getName)
            .toList();
    return new RouteUsageDto(
        PublicationType.TRIP,
        trip.getSlug(),
        trip.getName(),
        trip.getDateTime(),
        trip.zone().getId(),
        // The rule of TripDto.endDateOf: the last live stage's date, whatever the stage order.
        liveStages.stream().map(TripStage::getDateTime).max(Comparator.naturalOrder()).orElse(null),
        trip.getTeam().getSlug(),
        referencesRoute(trip.getRoute(), routeId),
        viaStages);
  }

  private static boolean referencesRoute(@Nullable Route route, Long routeId) {
    return route != null && routeId.equals(route.getId());
  }
}
