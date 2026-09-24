package fr.pedalons.service.migration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * Biketeam's team content as the mapping in {@link BiketeamMigrationService} consumes it, whatever
 * the source: the live export snapshot or the legacy dump. Field names follow biketeam's columns
 * ({@code deletion}, {@code point_lat}…), so both sources translate into the same shape.
 *
 * <p>Nothing about people lives here: users, roles, participants and messages are the legacy
 * import's alone and stay with its JDBC reader.
 */
public final class BiketeamModel {

  private BiketeamModel() {}

  /** {@code visibility} is one of PUBLIC, PUBLIC_UNLISTED, PRIVATE, PRIVATE_UNLISTED, USER. */
  public record BtTeam(
      String id,
      String name,
      @Nullable String city,
      @Nullable String country,
      @Nullable LocalDate createdAt,
      @Nullable String visibility,
      boolean deletion) {}

  /** Free-text presentation plus contact details, shown on the biketeam team home page. */
  public record BtTeamDescription(
      @Nullable String description,
      @Nullable String addressStreetLine,
      @Nullable String addressPostalCode,
      @Nullable String addressPostalCity,
      @Nullable String phoneNumber,
      @Nullable String email,
      @Nullable String facebook,
      @Nullable String twitter,
      @Nullable String instagram,
      @Nullable String other) {}

  public record BtPlace(
      String id,
      String teamId,
      String name,
      @Nullable String address,
      @Nullable String link,
      @Nullable Double pointLat,
      @Nullable Double pointLng,
      boolean startPlace,
      boolean endPlace) {}

  /**
   * {@code windDirection} is biketeam's Java enum name ({@code NORTH_EAST}); the legacy mapping
   * expected {@code NORTHEAST}, and both are accepted.
   */
  public record BtMap(
      String id,
      String teamId,
      String name,
      @Nullable String permalink,
      double length,
      @Nullable String type,
      double positiveElevation,
      double negativeElevation,
      @Nullable LocalDate postedAt,
      @Nullable Double startPointLat,
      @Nullable Double startPointLng,
      @Nullable Double endPointLat,
      @Nullable Double endPointLng,
      @Nullable String windDirection,
      boolean deletion,
      List<String> tags) {}

  public record BtRide(
      String id,
      String teamId,
      @Nullable String permalink,
      @Nullable LocalDate date,
      String title,
      @Nullable String description,
      @Nullable String type,
      @Nullable String publishedStatus,
      @Nullable Instant publishedAt,
      @Nullable String startPlaceId,
      @Nullable String endPlaceId,
      boolean listedInFeed,
      boolean deletion) {}

  public record BtRideGroup(
      String id,
      String rideId,
      String name,
      @Nullable Double averageSpeed,
      @Nullable LocalTime meetingTime,
      @Nullable String mapId) {}

  public record BtRideTemplate(
      String id,
      String teamId,
      String name,
      @Nullable String description,
      @Nullable String type,
      @Nullable Integer increment,
      @Nullable String startPlaceId,
      @Nullable String endPlaceId) {}

  public record BtRideGroupTemplate(
      String id,
      String rideTemplateId,
      String name,
      @Nullable Double averageSpeed,
      @Nullable LocalTime meetingTime) {}

  public record BtTrip(
      String id,
      String teamId,
      @Nullable String permalink,
      @Nullable LocalDate startDate,
      @Nullable LocalDate endDate,
      @Nullable LocalTime meetingTime,
      @Nullable String type,
      @Nullable String publishedStatus,
      @Nullable Instant publishedAt,
      String title,
      @Nullable String description,
      @Nullable String startPlaceId,
      @Nullable String endPlaceId,
      @Nullable String markdownPage,
      boolean listedInFeed,
      boolean deletion) {}

  public record BtTripStage(
      String id,
      String tripId,
      @Nullable LocalDate date,
      String name,
      @Nullable String mapId,
      boolean alternative) {}

  public record BtPublication(
      String id,
      String teamId,
      @Nullable String publishedStatus,
      String title,
      @Nullable Instant publishedAt,
      @Nullable String content,
      boolean imaged,
      boolean deletion) {}
}
