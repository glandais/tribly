package fr.pedalons.dto.routes.response;

import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
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
        ride.getTeam().getSlug(),
        referencesRoute(ride.getRoute(), routeId),
        viaGroups);
  }

  public static RouteUsageDto fromTrip(Trip trip, Long routeId) {
    List<String> viaStages =
        trip.getStages().stream()
            .filter(stage -> !stage.isDeleted())
            .filter(stage -> referencesRoute(stage.getRoute(), routeId))
            .map(TripStage::getName)
            .toList();
    return new RouteUsageDto(
        PublicationType.TRIP,
        trip.getSlug(),
        trip.getName(),
        trip.getDateTime(),
        trip.getTeam().getSlug(),
        referencesRoute(trip.getRoute(), routeId),
        viaStages);
  }

  private static boolean referencesRoute(@Nullable Route route, Long routeId) {
    return route != null && routeId.equals(route.getId());
  }
}
