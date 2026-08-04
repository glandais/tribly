package fr.pedalons.service.geocode;

import fr.pedalons.dto.geocode.GeocodeResultDto;
import fr.pedalons.infrastructure.i18n.LanguageResolver;
import fr.pedalons.infrastructure.nominatim.NominatimLookup;
import fr.pedalons.infrastructure.nominatim.NominatimPlace;
import fr.pedalons.service.security.annotation.Public;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/**
 * Address lookup, served from our host so the provider's terms can actually be met.
 *
 * <p>Two things a browser could not do are the whole point of this indirection: sending the
 * identifying {@code User-Agent} the Nominatim usage policy requires (a forbidden header for {@code
 * fetch}), and caching results across users so a shared instance isn't queried again for a query it
 * has already answered. The policy asks for both, and the front-end used to do neither.
 *
 * <p>{@code @Public} because there is no entity to authorise: the data is OpenStreetMap's and
 * identical for every caller. The endpoint is nonetheless behind a login — see {@code
 * GeocodeResource} for why.
 *
 * <p>The credit itself is not this service's job — it is a visible one, rendered next to the results
 * by each client.
 */
@ApplicationScoped
public class GeocodeService {

  @Inject NominatimLookup nominatimLookup;

  @Inject LanguageResolver languageResolver;

  /**
   * The places matching {@code query}, in the language of the current request.
   *
   * <p>A query shorter than three characters is answered without asking upstream: the clients
   * already gate on it, and spending a shared instance's budget on a prefix that matches half of
   * France would be the kind of use its policy asks us not to make.
   */
  @Public
  public List<GeocodeResultDto> search(String query) {
    String normalized = NominatimLookup.normalize(query);
    if (normalized.length() < 3) {
      return List.of();
    }
    List<NominatimPlace> places =
        nominatimLookup.search(normalized, languageResolver.getLanguage());
    return places.stream().map(GeocodeService::toDto).filter(Objects::nonNull).toList();
  }

  /** {@code null} for a hit missing a name or a coordinate — there is nothing to place on a map. */
  private static @Nullable GeocodeResultDto toDto(NominatimPlace place) {
    if (place.displayName() == null || place.lat() == null || place.lon() == null) {
      return null;
    }
    try {
      return new GeocodeResultDto(
          String.valueOf(place.placeId()),
          place.displayName(),
          Double.parseDouble(place.lat()),
          Double.parseDouble(place.lon()));
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
