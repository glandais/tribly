package fr.pedalons.infrastructure.openmeteo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jspecify.annotations.Nullable;

/**
 * The Open-Meteo forecast API. The base URL is {@code OPEN_METEO_URL} — the free endpoint by
 * default, {@code https://customer-api.open-meteo.com} with {@code OPEN_METEO_API_KEY} for a
 * commercial plan, no code change either way.
 *
 * <p>{@code Response} rather than a mapped body, as in {@code GitHubRestClient}: {@link
 * OpenMeteoGateway} reads the status itself (a 429 is not an ordinary failure) and the body's shape
 * depends on the number of locations — one object for one, an array for several.
 *
 * <p>Several locations go in one request as comma-separated {@code latitude}, {@code longitude} and
 * {@code elevation} lists. A null parameter is left out of the query string: {@code elevation} for
 * points without altitude, {@code apikey} on the free plan.
 *
 * <p>The full URL is never logged: with a key it carries a credential.
 */
@RegisterRestClient(configKey = "open-meteo")
@Path("/v1/forecast")
@ClientHeaderParam(name = "User-Agent", value = "${pedalons.weather.user-agent}")
public interface OpenMeteoClient {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  Response forecast(
      @QueryParam("latitude") String latitudes,
      @QueryParam("longitude") String longitudes,
      @QueryParam("elevation") @Nullable String elevations,
      @QueryParam("hourly") String hourly,
      @QueryParam("daily") String daily,
      @QueryParam("timezone") String timezone,
      @QueryParam("timeformat") String timeformat,
      @QueryParam("forecast_days") int forecastDays,
      @QueryParam("wind_speed_unit") String windSpeedUnit,
      @QueryParam("models") String models,
      @QueryParam("cell_selection") String cellSelection,
      @QueryParam("apikey") @Nullable String apiKey);
}
