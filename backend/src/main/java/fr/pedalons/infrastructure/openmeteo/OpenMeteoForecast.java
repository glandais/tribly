package fr.pedalons.infrastructure.openmeteo;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * One location of an Open-Meteo answer, parsed. Units as requested: °C, %, mm, km/h, degrees the
 * wind comes from.
 *
 * @param elevation the elevation the provider used (the one asked for, or its DEM's)
 * @param timezone the IANA zone of the location ({@code timezone=auto}); daily dates are local to it
 * @param hours hours with every mandatory value present — an hour the model leaves blank is dropped
 */
public record OpenMeteoForecast(
    double latitude,
    double longitude,
    @Nullable Double elevation,
    @Nullable String timezone,
    List<Hour> hours,
    List<Day> days) {

  public record Hour(
      Instant time,
      double temperature,
      double apparentTemperature,
      @Nullable Integer precipitationProbability,
      double precipitation,
      int weatherCode,
      double windSpeed,
      double windDirection,
      @Nullable Double windGusts) {}

  /** {@code sunrise}/{@code sunset} are null in polar day or night. */
  public record Day(LocalDate date, @Nullable Instant sunrise, @Nullable Instant sunset) {}
}
