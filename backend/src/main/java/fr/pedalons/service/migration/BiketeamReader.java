package fr.pedalons.service.migration;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/** Read-only JDBC accessor on the named "biketeam" datasource (restored biketeam dump). */
@ApplicationScoped
public class BiketeamReader {

  @Inject
  @DataSource("biketeam")
  AgroalDataSource ds;

  // ─── Records ──────────────────────────────────────────────────────────────

  public record BtTeam(
      String id, String name, String city, String country, LocalDate createdAt, boolean deletion) {}

  /**
   * {@code email} is null for the many biketeam accounts created through a Strava/Facebook/Google
   * login, where biketeam never asked for one. The external ids let the migration synthesize a
   * stable placeholder address for them.
   */
  public record BtUser(
      String id,
      @Nullable String email,
      String firstName,
      String lastName,
      @Nullable String city,
      boolean admin,
      boolean deletion,
      @Nullable Long stravaId,
      @Nullable String facebookId,
      @Nullable String googleId) {}

  public record BtUserRole(String userId, String teamId, String role) {}

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

  public record BtMap(
      String id,
      String teamId,
      String name,
      @Nullable String permalink,
      double length,
      String type,
      double positiveElevation,
      double negativeElevation,
      LocalDate postedAt,
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
      LocalDate date,
      String title,
      @Nullable String description,
      String type,
      String publishedStatus,
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

  public record BtRideGroupParticipant(String rideGroupId, String userId) {}

  public record BtRideTemplate(
      String id,
      String teamId,
      String name,
      @Nullable String description,
      String type,
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
      LocalDate startDate,
      LocalDate endDate,
      @Nullable LocalTime meetingTime,
      String type,
      String publishedStatus,
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
      LocalDate date,
      String name,
      @Nullable String mapId,
      boolean alternative) {}

  public record BtTripParticipant(String tripId, String userId) {}

  public record BtPublication(
      String id,
      String teamId,
      String publishedStatus,
      String title,
      Instant publishedAt,
      @Nullable String content,
      boolean imaged,
      boolean deletion) {}

  public record BtMessage(
      String id,
      String teamId,
      String targetId,
      @Nullable String replyToId,
      String type,
      String userId,
      Instant publishedAt,
      String content) {}

  // ─── Connectivity ─────────────────────────────────────────────────────────

  /** Throws if the biketeam datasource is unreachable. Call before iterating reads. */
  public void verifyConnectivity() throws SQLException {
    try (Connection c = ds.getConnection();
        PreparedStatement ps = c.prepareStatement("SELECT 1")) {
      try (ResultSet rs = ps.executeQuery()) {
        rs.next();
      }
    }
  }

  // ─── Queries ──────────────────────────────────────────────────────────────

  public @Nullable BtTeam findTeam(String teamId) {
    return one(
        "SELECT id, name, city, country, created_at, deletion FROM team WHERE id = ?",
        ps -> ps.setString(1, teamId),
        rs ->
            new BtTeam(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                toLocalDate(rs.getDate(5)),
                rs.getBoolean(6)));
  }

  public List<BtUser> findUsersByIds(List<String> ids) {
    if (ids.isEmpty()) {
      return List.of();
    }
    String placeholders = String.join(",", ids.stream().map(x -> "?").toList());
    String sql =
        "SELECT id, email, first_name, last_name, city, admin, deletion, "
            + "strava_id, facebook_id, google_id FROM user_account "
            + "WHERE id IN ("
            + placeholders
            + ")";
    return many(
        sql,
        ps -> {
          for (int i = 0; i < ids.size(); i++) {
            ps.setString(i + 1, ids.get(i));
          }
        },
        rs -> {
          String id = rs.getString(1);
          String email = rs.getString(2);
          String firstName = rs.getString(3);
          String lastName = rs.getString(4);
          String city = rs.getString(5);
          boolean admin = rs.getBoolean(6);
          boolean deletion = rs.getBoolean(7);
          // wasNull() reports on the column just read, so it has to be consulted right here.
          long strava = rs.getLong(8);
          Long stravaId = rs.wasNull() ? null : strava;
          return new BtUser(
              id,
              email,
              firstName,
              lastName,
              city,
              admin,
              deletion,
              stravaId,
              rs.getString(9),
              rs.getString(10));
        });
  }

  public List<BtUserRole> findUserRoles(String teamId) {
    return many(
        "SELECT user_id, team_id, role FROM user_role WHERE team_id = ?",
        ps -> ps.setString(1, teamId),
        rs -> new BtUserRole(rs.getString(1), rs.getString(2), rs.getString(3)));
  }

  public List<BtPlace> findPlaces(String teamId) {
    return many(
        "SELECT id, team_id, name, address, link, point_lat, point_lng, start_place, end_place"
            + " FROM place WHERE team_id = ?",
        ps -> ps.setString(1, teamId),
        rs ->
            new BtPlace(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5),
                getNullableDouble(rs, 6),
                getNullableDouble(rs, 7),
                rs.getBoolean(8),
                rs.getBoolean(9)));
  }

  public List<BtMap> findMaps(String teamId) {
    List<BtMap> bare =
        many(
            "SELECT id, team_id, name, permalink, length, type, positive_elevation,"
                + " negative_elevation, posted_at, start_point_lat, start_point_lng,"
                + " end_point_lat, end_point_lng, wind_direction, deletion"
                + " FROM map WHERE team_id = ?",
            ps -> ps.setString(1, teamId),
            rs ->
                new BtMap(
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getDouble(5),
                    rs.getString(6),
                    rs.getDouble(7),
                    rs.getDouble(8),
                    toLocalDate(rs.getDate(9)),
                    getNullableDouble(rs, 10),
                    getNullableDouble(rs, 11),
                    getNullableDouble(rs, 12),
                    getNullableDouble(rs, 13),
                    rs.getString(14),
                    rs.getBoolean(15),
                    new ArrayList<>()));

    Map<String, List<String>> tagsByMapId = readMapTags();
    List<BtMap> out = new ArrayList<>(bare.size());
    for (BtMap m : bare) {
      List<String> tags = tagsByMapId.getOrDefault(m.id(), List.of());
      out.add(
          new BtMap(
              m.id(),
              m.teamId(),
              m.name(),
              m.permalink(),
              m.length(),
              m.type(),
              m.positiveElevation(),
              m.negativeElevation(),
              m.postedAt(),
              m.startPointLat(),
              m.startPointLng(),
              m.endPointLat(),
              m.endPointLng(),
              m.windDirection(),
              m.deletion(),
              tags));
    }
    return out;
  }

  private Map<String, List<String>> readMapTags() {
    Map<String, List<String>> result = new HashMap<>();
    forEach(
        "SELECT map_id, tags FROM map_tags",
        ps -> {},
        rs -> {
          String mapId = rs.getString(1);
          String tag = rs.getString(2);
          if (tag != null && !tag.isBlank()) {
            result.computeIfAbsent(mapId, k -> new ArrayList<>()).add(tag);
          }
        });
    return result;
  }

  public List<BtRide> findRides(String teamId) {
    return many(
        "SELECT id, team_id, permalink, date, title, description, type, published_status,"
            + " published_at, start_place_id, end_place_id, listed_in_feed, deletion"
            + " FROM ride WHERE team_id = ?",
        ps -> ps.setString(1, teamId),
        rs ->
            new BtRide(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                toLocalDate(rs.getDate(4)),
                rs.getString(5),
                rs.getString(6),
                rs.getString(7),
                rs.getString(8),
                toInstant(rs.getObject(9, OffsetDateTime.class)),
                rs.getString(10),
                rs.getString(11),
                rs.getBoolean(12),
                rs.getBoolean(13)));
  }

  public List<BtRideGroup> findRideGroups(List<String> rideIds) {
    if (rideIds.isEmpty()) {
      return List.of();
    }
    String placeholders = String.join(",", rideIds.stream().map(x -> "?").toList());
    String sql =
        "SELECT id, ride_id, name, average_speed, meeting_time, map_id"
            + " FROM ride_group WHERE ride_id IN ("
            + placeholders
            + ")";
    return many(
        sql,
        ps -> {
          for (int i = 0; i < rideIds.size(); i++) {
            ps.setString(i + 1, rideIds.get(i));
          }
        },
        rs ->
            new BtRideGroup(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                getNullableDouble(rs, 4),
                toLocalTime(rs.getTime(5)),
                rs.getString(6)));
  }

  public List<BtRideGroupParticipant> findRideGroupParticipants(List<String> rideGroupIds) {
    if (rideGroupIds.isEmpty()) {
      return List.of();
    }
    String placeholders = String.join(",", rideGroupIds.stream().map(x -> "?").toList());
    String sql =
        "SELECT ride_group_id, user_id FROM ride_group_participant WHERE ride_group_id IN ("
            + placeholders
            + ")";
    return many(
        sql,
        ps -> {
          for (int i = 0; i < rideGroupIds.size(); i++) {
            ps.setString(i + 1, rideGroupIds.get(i));
          }
        },
        rs -> new BtRideGroupParticipant(rs.getString(1), rs.getString(2)));
  }

  public List<BtRideTemplate> findRideTemplates(String teamId) {
    return many(
        "SELECT id, team_id, name, description, type, increment, start_place_id, end_place_id"
            + " FROM ride_template WHERE team_id = ?",
        ps -> ps.setString(1, teamId),
        rs ->
            new BtRideTemplate(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5),
                getNullableInt(rs, 6),
                rs.getString(7),
                rs.getString(8)));
  }

  public List<BtRideGroupTemplate> findRideGroupTemplates(List<String> templateIds) {
    if (templateIds.isEmpty()) {
      return List.of();
    }
    String placeholders = String.join(",", templateIds.stream().map(x -> "?").toList());
    String sql =
        "SELECT id, ride_template_id, name, average_speed, meeting_time"
            + " FROM ride_group_template WHERE ride_template_id IN ("
            + placeholders
            + ")";
    return many(
        sql,
        ps -> {
          for (int i = 0; i < templateIds.size(); i++) {
            ps.setString(i + 1, templateIds.get(i));
          }
        },
        rs ->
            new BtRideGroupTemplate(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                getNullableDouble(rs, 4),
                toLocalTime(rs.getTime(5))));
  }

  public List<BtTrip> findTrips(String teamId) {
    return many(
        "SELECT id, team_id, permalink, start_date, end_date, meeting_time, type,"
            + " published_status, published_at, title, description, start_place_id,"
            + " end_place_id, markdown_page, listed_in_feed, deletion"
            + " FROM trip WHERE team_id = ?",
        ps -> ps.setString(1, teamId),
        rs ->
            new BtTrip(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                toLocalDate(rs.getDate(4)),
                toLocalDate(rs.getDate(5)),
                toLocalTime(rs.getTime(6)),
                rs.getString(7),
                rs.getString(8),
                toInstant(rs.getObject(9, OffsetDateTime.class)),
                rs.getString(10),
                rs.getString(11),
                rs.getString(12),
                rs.getString(13),
                rs.getString(14),
                rs.getBoolean(15),
                rs.getBoolean(16)));
  }

  public List<BtTripStage> findTripStages(List<String> tripIds) {
    if (tripIds.isEmpty()) {
      return List.of();
    }
    String placeholders = String.join(",", tripIds.stream().map(x -> "?").toList());
    String sql =
        "SELECT id, trip_id, date, name, map_id, alternative FROM trip_stage WHERE trip_id IN ("
            + placeholders
            + ") ORDER BY id ASC";
    return many(
        sql,
        ps -> {
          for (int i = 0; i < tripIds.size(); i++) {
            ps.setString(i + 1, tripIds.get(i));
          }
        },
        rs ->
            new BtTripStage(
                rs.getString(1),
                rs.getString(2),
                toLocalDate(rs.getDate(3)),
                rs.getString(4),
                rs.getString(5),
                rs.getBoolean(6)));
  }

  public List<BtTripParticipant> findTripParticipants(List<String> tripIds) {
    if (tripIds.isEmpty()) {
      return List.of();
    }
    String placeholders = String.join(",", tripIds.stream().map(x -> "?").toList());
    String sql =
        "SELECT trip_id, user_id FROM trip_participant WHERE trip_id IN (" + placeholders + ")";
    return many(
        sql,
        ps -> {
          for (int i = 0; i < tripIds.size(); i++) {
            ps.setString(i + 1, tripIds.get(i));
          }
        },
        rs -> new BtTripParticipant(rs.getString(1), rs.getString(2)));
  }

  public List<BtPublication> findPublications(String teamId) {
    return many(
        "SELECT id, team_id, published_status, title, published_at, content, imaged, deletion"
            + " FROM publication WHERE team_id = ?",
        ps -> ps.setString(1, teamId),
        rs ->
            new BtPublication(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                toInstant(rs.getObject(5, OffsetDateTime.class)),
                rs.getString(6),
                rs.getBoolean(7),
                rs.getBoolean(8)));
  }

  public List<BtMessage> findMessages(String teamId) {
    return many(
        "SELECT id, team_id, target_id, reply_to_id, type, user_id, published_at, content"
            + " FROM message WHERE team_id = ?",
        ps -> ps.setString(1, teamId),
        rs ->
            new BtMessage(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5),
                rs.getString(6),
                toInstant(rs.getObject(7, OffsetDateTime.class)),
                rs.getString(8)));
  }

  // ─── JDBC plumbing ────────────────────────────────────────────────────────

  @FunctionalInterface
  private interface PsBinder {
    void bind(PreparedStatement ps) throws SQLException;
  }

  @FunctionalInterface
  private interface RowMapper<T> {
    T map(ResultSet rs) throws SQLException;
  }

  @FunctionalInterface
  private interface RowConsumer {
    void accept(ResultSet rs) throws SQLException;
  }

  private <T> @Nullable T one(String sql, PsBinder binder, RowMapper<T> mapper) {
    try (Connection c = ds.getConnection();
        PreparedStatement ps = c.prepareStatement(sql)) {
      binder.bind(ps);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? mapper.map(rs) : null;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Biketeam read failed: " + sql, e);
    }
  }

  private <T> List<T> many(String sql, PsBinder binder, RowMapper<T> mapper) {
    List<T> out = new ArrayList<>();
    try (Connection c = ds.getConnection();
        PreparedStatement ps = c.prepareStatement(sql)) {
      binder.bind(ps);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          out.add(mapper.map(rs));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Biketeam read failed: " + sql, e);
    }
    return out;
  }

  private void forEach(String sql, PsBinder binder, RowConsumer consumer) {
    try (Connection c = ds.getConnection();
        PreparedStatement ps = c.prepareStatement(sql)) {
      binder.bind(ps);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          consumer.accept(rs);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Biketeam read failed: " + sql, e);
    }
  }

  private static @Nullable Double getNullableDouble(ResultSet rs, int idx) throws SQLException {
    double v = rs.getDouble(idx);
    return rs.wasNull() ? null : v;
  }

  private static @Nullable Integer getNullableInt(ResultSet rs, int idx) throws SQLException {
    int v = rs.getInt(idx);
    return rs.wasNull() ? null : v;
  }

  private static @Nullable LocalDate toLocalDate(java.sql.@Nullable Date d) {
    return d == null ? null : d.toLocalDate();
  }

  private static @Nullable LocalTime toLocalTime(java.sql.@Nullable Time t) {
    return t == null ? null : t.toLocalTime();
  }

  private static @Nullable Instant toInstant(@Nullable OffsetDateTime odt) {
    return odt == null ? null : odt.toInstant();
  }
}
