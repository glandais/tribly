package fr.pedalons.domain.weather;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/**
 * One forecast hour of a {@link WeatherCell}. Units as asked from Open-Meteo: °C, %, mm, km/h, and
 * degrees for the direction the wind comes <em>from</em>.
 *
 * <p>Global like its cell (see the package documentation). Written by a native upsert, rewritten
 * whole at every refresh, purged a day after the hour has passed. {@code cellId} is a plain column
 * rather than an association: nothing navigates from an hour to its cell, and the foreign key with
 * {@code ON DELETE CASCADE} is declared by the migration (and by {@code import-test.sql}).
 */
@Setter
@Getter
@Entity
@Table(
    name = "weather_hourly",
    indexes = {@Index(name = "idx_weather_hourly_time", columnList = "time")})
@IdClass(WeatherHourly.Key.class)
@NoArgsConstructor
public class WeatherHourly {

  @Id
  @Column(name = "cell_id", nullable = false)
  private Long cellId;

  /** Start of the hour, as the provider sends it. */
  @Id
  @Column(name = "time", nullable = false)
  private Instant time;

  @Column(name = "temperature", nullable = false)
  private double temperature;

  @Column(name = "apparent_temperature", nullable = false)
  private double apparentTemperature;

  /** Not every model provides it; null then. */
  @Column(name = "precipitation_probability")
  private @Nullable Integer precipitationProbability;

  @Column(name = "precipitation", nullable = false)
  private double precipitation;

  /** WMO weather interpretation code. */
  @Column(name = "weather_code", nullable = false)
  private int weatherCode;

  @Column(name = "wind_speed", nullable = false)
  private double windSpeed;

  @Column(name = "wind_direction", nullable = false)
  private double windDirection;

  @Column(name = "wind_gusts")
  private @Nullable Double windGusts;

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Key implements Serializable {
    private Long cellId;
    private Instant time;

    public Key(Long cellId, Instant time) {
      this.cellId = cellId;
      this.time = time;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Key key)) return false;
      return Objects.equals(cellId, key.cellId) && Objects.equals(time, key.time);
    }

    @Override
    public int hashCode() {
      return Objects.hash(cellId, time);
    }
  }
}
