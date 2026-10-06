package fr.pedalons.domain.weather;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/**
 * Sunrise and sunset of a {@link WeatherCell} for one local date — the cell's own time zone, as the
 * provider reports it. What tells a night ride from a day ride.
 *
 * <p>Global like its cell (see the package documentation); same storage rules as {@link
 * WeatherHourly}.
 */
@Setter
@Getter
@Entity
@Table(
    name = "weather_daily",
    indexes = {@Index(name = "idx_weather_daily_date", columnList = "date")})
@IdClass(WeatherDaily.Key.class)
@NoArgsConstructor
public class WeatherDaily {

  @Id
  @Column(name = "cell_id", nullable = false)
  private Long cellId;

  @Id
  @Column(name = "date", nullable = false)
  private LocalDate date;

  /** Null in polar day or night. */
  @Column(name = "sunrise")
  private @Nullable Instant sunrise;

  @Column(name = "sunset")
  private @Nullable Instant sunset;

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Key implements Serializable {
    private Long cellId;
    private LocalDate date;

    public Key(Long cellId, LocalDate date) {
      this.cellId = cellId;
      this.date = date;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Key key)) return false;
      return Objects.equals(cellId, key.cellId) && Objects.equals(date, key.date);
    }

    @Override
    public int hashCode() {
      return Objects.hash(cellId, date);
    }
  }
}
