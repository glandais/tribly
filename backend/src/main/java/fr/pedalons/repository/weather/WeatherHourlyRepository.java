package fr.pedalons.repository.weather;

import fr.pedalons.domain.weather.WeatherHourly;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.jspecify.annotations.Nullable;

/**
 * Forecast hours of the weather cells. Global like the cells (see {@code
 * fr.pedalons.domain.weather}): no query here filters by domain, and every read starts from a ride
 * the caller may already read.
 */
@ApplicationScoped
public class WeatherHourlyRepository
    implements PanacheRepositoryBase<WeatherHourly, WeatherHourly.Key> {

  /** Rows per statement: 10 binds each, far below PostgreSQL's 65535. */
  static final int UPSERT_CHUNK = 200;

  /**
   * The read side's hour columns, joined with the sunrise and sunset of the hour's local date in
   * the cell's zone. Selected as {@code h.*} positions 0–11 after the owner id.
   */
  private static final String HOUR_COLUMNS =
      """
      h.time, h.temperature, h.apparent_temperature, h.precipitation_probability,
      h.precipitation, h.weather_code, h.wind_speed, h.wind_direction, h.wind_gusts,
      d.sunrise, d.sunset
      """;

  private static final String DAILY_JOIN =
      """
      left join weather_daily d on d.cell_id = h.cell_id
        and d.date = cast(h.time at time zone coalesce(c.timezone, 'UTC') as date)
      """;

  /**
   * Writes a refreshed forecast. Native {@code ON CONFLICT DO UPDATE}: a refresh rewrites the hours
   * it already had and adds the new ones in one statement per {@value #UPSERT_CHUNK} hours, with no
   * read first. Hours the provider no longer sends stay until the purge — they are in the past.
   */
  public void upsert(Long cellId, List<WeatherHourly> hours) {
    for (int from = 0; from < hours.size(); from += UPSERT_CHUNK) {
      List<WeatherHourly> chunk = hours.subList(from, Math.min(hours.size(), from + UPSERT_CHUNK));
      StringBuilder sql =
          new StringBuilder(
              "insert into weather_hourly (cell_id, time, temperature, apparent_temperature,"
                  + " precipitation_probability, precipitation, weather_code, wind_speed,"
                  + " wind_direction, wind_gusts) values ");
      for (int i = 0; i < chunk.size(); i++) {
        if (i > 0) {
          sql.append(", ");
        }
        sql.append(
            "(:cell, :t%1$d, :tp%1$d, :at%1$d, :pp%1$d, :pr%1$d, :wc%1$d, :ws%1$d, :wd%1$d, :wg%1$d)"
                .formatted(i));
      }
      sql.append(
          """
           on conflict (cell_id, time) do update set
            temperature = excluded.temperature,
            apparent_temperature = excluded.apparent_temperature,
            precipitation_probability = excluded.precipitation_probability,
            precipitation = excluded.precipitation,
            weather_code = excluded.weather_code,
            wind_speed = excluded.wind_speed,
            wind_direction = excluded.wind_direction,
            wind_gusts = excluded.wind_gusts
          """);
      Query query = getEntityManager().createNativeQuery(sql.toString());
      query.setParameter("cell", cellId);
      for (int i = 0; i < chunk.size(); i++) {
        WeatherHourly hour = chunk.get(i);
        query.setParameter("t" + i, Timestamp.from(hour.getTime()));
        query.setParameter("tp" + i, hour.getTemperature());
        query.setParameter("at" + i, hour.getApparentTemperature());
        query.setParameter(
            "pp" + i,
            new TypedParameterValue<>(
                StandardBasicTypes.INTEGER, hour.getPrecipitationProbability()));
        query.setParameter("pr" + i, hour.getPrecipitation());
        query.setParameter("wc" + i, hour.getWeatherCode());
        query.setParameter("ws" + i, hour.getWindSpeed());
        query.setParameter("wd" + i, hour.getWindDirection());
        query.setParameter(
            "wg" + i, new TypedParameterValue<>(StandardBasicTypes.DOUBLE, hour.getWindGusts()));
      }
      query.executeUpdate();
    }
  }

  /** The housekeeping: hours that ended before {@code cutoff}. */
  public long deleteBefore(Instant cutoff) {
    return delete("time < ?1", cutoff);
  }

  /**
   * The detail path: the hours of {@code cellIds} within {@code [from, to]}, with sunrise and
   * sunset. One query, whatever the number of cells — hence of groups.
   */
  public List<WeatherHourRow> findHours(Collection<Long> cellIds, Instant from, Instant to) {
    if (cellIds.isEmpty()) {
      return List.of();
    }
    @SuppressWarnings("unchecked")
    List<Object[]> rows =
        getEntityManager()
            .createNativeQuery(
                "select h.cell_id, "
                    + HOUR_COLUMNS
                    + " from weather_hourly h join weather_cells c on c.id = h.cell_id "
                    + DAILY_JOIN
                    + " where h.cell_id in (:cellIds) and h.time >= :from and h.time <= :to"
                    + " order by h.cell_id, h.time")
            .setParameter("cellIds", cellIds)
            .setParameter("from", Timestamp.from(from))
            .setParameter("to", Timestamp.from(to))
            .getResultList();
    List<WeatherHourRow> hours = new ArrayList<>(rows.size());
    for (Object[] row : rows) {
      hours.add(hourRow(row, 0));
    }
    return hours;
  }

  /**
   * One ride's window for the list summary, and the hours of its departure cell within it.
   *
   * @param lastArrival the estimated arrival of the latest group
   * @param fetchedAt when the departure cell was last fetched
   */
  public record RideWindowHour(
      long rideId,
      Instant departure,
      Instant lastArrival,
      @Nullable Instant fetchedAt,
      WeatherHourRow hour) {}

  /**
   * The list path: for each ride of a page, the hours of its departure cell from its start to the
   * estimated arrival of its last group, in <b>one</b> query whatever the page size.
   *
   * <p>Everything the Java detail path works out from the entities is spelled here in SQL, so the
   * list never loads a ride's groups:
   *
   * <ul>
   *   <li>the departure point: the meeting point, else the ride's route start, else the start of the
   *       first group's route (by {@code sort_order}) — deleted routes skipped;
   *   <li>its cell, by {@code cellLatIdxSql} / {@code cellLonIdxSql} ({@code CellKey.SQL_*_IDX}),
   *       without elevation band;
   *   <li>each group's start: its {@code time} at the departure's local date, in the departure
   *       cell's zone as the provider reported it (the detail uses {@code TimezoneService}; the two
   *       agree but for a border village), else the ride's;
   *   <li>its arrival: the start plus the distance of its route (else the ride's) at its average
   *       speed (else {@code defaultSpeed}); a ride without groups rides its own route at the
   *       default speed.
   * </ul>
   *
   * <p>A ride without a departure point, or whose cell was never fetched, returns no row.
   *
   * @param slack how far outside the window an hour is still read, so the hour nearest the start
   *     and the end is in the result
   */
  public List<RideWindowHour> findRideWindowHours(
      Collection<Long> rideIds,
      String cellLatIdxSql,
      String cellLonIdxSql,
      double defaultSpeed,
      java.time.Duration slack) {
    if (rideIds.isEmpty()) {
      return List.of();
    }
    String latIdx = cellLatIdxSql.replace("?1", "r.geom");
    String lonIdx = cellLonIdxSql.replace("?1", "r.geom");
    String zone = "coalesce(rc.timezone, 'UTC')";
    String sql =
        """
        with r as (
          select te.id as ride_id, te.date_time as departure, rr.distance as ride_distance,
                 coalesce(p.geometry, rr."start",
                   (select gr."start" from ride_groups g
                      join team_entities gr on gr.id = g.route_id and gr.deleted = false
                     where g.ride_id = te.id and gr."start" is not null
                     order by g.sort_order, g.id limit 1)) as geom
          from team_entities te
          left join places p on p.id = te.place_start_id
          left join team_entities rr on rr.id = te.route_id and rr.deleted = false
          where te.id in (:rideIds)
        ),
        rc as (
          select r.ride_id, r.departure, r.ride_distance, c.id as cell_id, c.timezone, c.fetched_at
          from r join weather_cells c
            on c.lat_idx = %1$s and c.lon_idx = %2$s and c.ele_band is null
          where r.geom is not null
        ),
        w as (
          select rc.*, greatest(rc.departure, coalesce(
            (select max(
                case when g.time is null then rc.departure
                     else (cast(rc.departure at time zone %3$s as date) + g.time) at time zone %3$s
                end
                + make_interval(secs => coalesce(gr.distance, rc.ride_distance, 0) * 3.6
                    / case when g.average_speed > 0 then g.average_speed else :defaultSpeed end))
               from ride_groups g
               left join team_entities gr on gr.id = g.route_id and gr.deleted = false
              where g.ride_id = rc.ride_id),
            rc.departure + make_interval(secs => coalesce(rc.ride_distance, 0) * 3.6 / :defaultSpeed)
          )) as last_arrival
          from rc
        )
        select w.ride_id, w.departure, w.last_arrival, w.fetched_at, %4$s
        from w
        join weather_hourly h on h.cell_id = w.cell_id
          and h.time >= w.departure - make_interval(secs => :slack)
          and h.time <= w.last_arrival + make_interval(secs => :slack)
        join weather_cells c on c.id = w.cell_id
        %5$s
        order by w.ride_id, h.time
        """
            .formatted(latIdx, lonIdx, zone, HOUR_COLUMNS, DAILY_JOIN);
    @SuppressWarnings("unchecked")
    List<Object[]> rows =
        getEntityManager()
            .createNativeQuery(sql)
            .setParameter("rideIds", rideIds)
            .setParameter("defaultSpeed", defaultSpeed)
            .setParameter("slack", (double) slack.toSeconds())
            .getResultList();
    List<RideWindowHour> hours = new ArrayList<>(rows.size());
    for (Object[] row : rows) {
      hours.add(
          new RideWindowHour(
              ((Number) row[0]).longValue(),
              instant(row[1]),
              instant(row[2]),
              nullableInstant(row[3]),
              hourRow(row, 0, 4)));
    }
    return hours;
  }

  private static WeatherHourRow hourRow(Object[] row, int ownerColumn) {
    return hourRow(row, ownerColumn, ownerColumn + 1);
  }

  /** Reads {@link #HOUR_COLUMNS} starting at column {@code first}. */
  private static WeatherHourRow hourRow(Object[] row, int ownerColumn, int first) {
    return new WeatherHourRow(
        ((Number) row[ownerColumn]).longValue(),
        instant(row[first]),
        ((Number) row[first + 1]).doubleValue(),
        ((Number) row[first + 2]).doubleValue(),
        row[first + 3] == null ? null : ((Number) row[first + 3]).intValue(),
        ((Number) row[first + 4]).doubleValue(),
        ((Number) row[first + 5]).intValue(),
        ((Number) row[first + 6]).doubleValue(),
        ((Number) row[first + 7]).doubleValue(),
        row[first + 8] == null ? null : ((Number) row[first + 8]).doubleValue(),
        nullableInstant(row[first + 9]),
        nullableInstant(row[first + 10]));
  }

  /** A {@code timestamptz} column, whichever type the driver hands it as. */
  static Instant instant(Object value) {
    return switch (value) {
      case Instant i -> i;
      case Timestamp t -> t.toInstant();
      case OffsetDateTime o -> o.toInstant();
      default -> throw new IllegalStateException("Not a timestamp: " + value.getClass());
    };
  }

  static @Nullable Instant nullableInstant(@Nullable Object value) {
    return value == null ? null : instant(value);
  }
}
