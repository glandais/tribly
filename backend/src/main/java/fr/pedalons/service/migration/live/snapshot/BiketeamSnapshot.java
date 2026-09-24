package fr.pedalons.service.migration.live.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * {@code GET /internal/pedalons/teams/{teamId}/snapshot}, as biketeam serves it — the contract of
 * docs/plans/2026-09-22-biketeam-live-migration.md §6.2, field for field. Unknown fields are
 * ignored so biketeam can add without breaking; lists are expected empty rather than null, but a
 * null one is read as empty all the same.
 *
 * <p>Top-level lists come sorted by id; nested ones (ride groups, trip stages, template groups) in
 * biketeam's display order, which the mapping turns into {@code sortOrder}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BiketeamSnapshot(
    int schemaVersion,
    @Nullable Instant generatedAt,
    @Nullable Team team,
    @Nullable List<Place> places,
    @Nullable List<MapItem> maps,
    @Nullable List<RideTemplate> rideTemplates,
    @Nullable List<Publication> publications,
    @Nullable List<Ride> rides,
    @Nullable List<Trip> trips) {

  public static final int SCHEMA_VERSION = 1;

  public List<Place> placesOrEmpty() {
    return places == null ? List.of() : places;
  }

  public List<MapItem> mapsOrEmpty() {
    return maps == null ? List.of() : maps;
  }

  public List<RideTemplate> rideTemplatesOrEmpty() {
    return rideTemplates == null ? List.of() : rideTemplates;
  }

  public List<Publication> publicationsOrEmpty() {
    return publications == null ? List.of() : publications;
  }

  public List<Ride> ridesOrEmpty() {
    return rides == null ? List.of() : rides;
  }

  public List<Trip> tripsOrEmpty() {
    return trips == null ? List.of() : trips;
  }

  /** A file biketeam serves from {@code path}, relative to its export host. */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record FileRef(
      String kind,
      String path,
      String fileName,
      @Nullable String contentType,
      long size,
      @Nullable String md5) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Team(
      String id,
      String name,
      @Nullable String city,
      @Nullable String country,
      @Nullable LocalDate createdAt,
      @Nullable String visibility,
      @Nullable String timezone,
      @Nullable Description description,
      @Nullable String markdownPage,
      @Nullable FileRef logo) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Description(
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

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Place(
      String id,
      String name,
      @Nullable String address,
      @Nullable String link,
      @Nullable Double lat,
      @Nullable Double lng,
      boolean startPlace,
      boolean endPlace) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Point(@Nullable Double lat, @Nullable Double lng) {}

  /** A biketeam {@code map} — a Pédalons route. */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record MapItem(
      String id,
      @Nullable String permalink,
      String name,
      double length,
      @Nullable String type,
      double positiveElevation,
      double negativeElevation,
      @Nullable LocalDate postedAt,
      @Nullable Point startPoint,
      @Nullable Point endPoint,
      @Nullable String windDirection,
      @Nullable List<String> tags,
      boolean deleted,
      @Nullable FileRef gpx) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record RideTemplate(
      String id,
      String name,
      @Nullable String description,
      @Nullable String type,
      @Nullable Integer increment,
      @Nullable String startPlaceId,
      @Nullable String endPlaceId,
      @Nullable List<RideTemplateGroup> groups) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record RideTemplateGroup(
      String id, String name, @Nullable Double averageSpeed, @Nullable LocalTime meetingTime) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Publication(
      String id,
      String title,
      @Nullable String content,
      @Nullable String publishedStatus,
      @Nullable Instant publishedAt,
      boolean deleted,
      @Nullable FileRef image) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Ride(
      String id,
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
      boolean deleted,
      @Nullable FileRef image,
      @Nullable List<RideGroup> groups) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record RideGroup(
      String id,
      String name,
      @Nullable Double averageSpeed,
      @Nullable LocalTime meetingTime,
      @Nullable String mapId) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Trip(
      String id,
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
      boolean deleted,
      @Nullable FileRef image,
      @Nullable List<TripStage> stages) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record TripStage(
      String id,
      @Nullable LocalDate date,
      String name,
      @Nullable String mapId,
      boolean alternative) {}
}
