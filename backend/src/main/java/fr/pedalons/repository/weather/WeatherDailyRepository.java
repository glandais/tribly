package fr.pedalons.repository.weather;

import fr.pedalons.domain.weather.WeatherDaily;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.jspecify.annotations.Nullable;

/** Sunrise and sunset of the weather cells. Global like the cells — no domain filter. */
@ApplicationScoped
public class WeatherDailyRepository
    implements PanacheRepositoryBase<WeatherDaily, WeatherDaily.Key> {

  /** Same native upsert as {@code WeatherHourlyRepository#upsert}; a forecast has ~8 days. */
  public void upsert(Long cellId, List<WeatherDaily> days) {
    if (days.isEmpty()) {
      return;
    }
    StringBuilder sql =
        new StringBuilder("insert into weather_daily (cell_id, date, sunrise, sunset) values ");
    for (int i = 0; i < days.size(); i++) {
      if (i > 0) {
        sql.append(", ");
      }
      sql.append("(:cell, :d%1$d, :sr%1$d, :ss%1$d)".formatted(i));
    }
    sql.append(
        " on conflict (cell_id, date) do update set sunrise = excluded.sunrise,"
            + " sunset = excluded.sunset");
    Query query = getEntityManager().createNativeQuery(sql.toString());
    query.setParameter("cell", cellId);
    for (int i = 0; i < days.size(); i++) {
      WeatherDaily day = days.get(i);
      query.setParameter("d" + i, day.getDate());
      query.setParameter("sr" + i, timestamp(day.getSunrise()));
      query.setParameter("ss" + i, timestamp(day.getSunset()));
    }
    query.executeUpdate();
  }

  /** The housekeeping: dates before {@code cutoff}. */
  public long deleteBefore(LocalDate cutoff) {
    return delete("date < ?1", cutoff);
  }

  private static TypedParameterValue<Instant> timestamp(@Nullable Instant value) {
    return new TypedParameterValue<>(StandardBasicTypes.INSTANT, value);
  }
}
