package fr.pedalons.infrastructure.nominatim;

import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Locale;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 * The cached call to the geocoder.
 *
 * <p>Its own bean rather than a method of {@code GeocodeService} because {@code @CacheResult} is an
 * interceptor: a call from a sibling method of the same bean bypasses the CDI proxy and the cache
 * silently never fires. Resolving the request's language and caching the lookup therefore cannot
 * live in one class — and the cache is not an optimisation here, it is half of what the Nominatim
 * usage policy asks for.
 */
@ApplicationScoped
public class NominatimLookup {

  private static final Logger LOG = Logger.getLogger(NominatimLookup.class);

  /** What the autocomplete shows. Asking for more would be requesting work nobody reads. */
  private static final int LIMIT = 5;

  @Inject @RestClient NominatimClient nominatimClient;

  /**
   * The places matching {@code query}, at most {@value #LIMIT} of them.
   *
   * <p>Cached on the <em>normalised</em> query and the language: an autocomplete sends every prefix
   * of what is being typed, and several users of the same instance type the same town names.
   * Normalising before the key is computed is what makes it hit — {@code "Lyon "} and {@code "lyon"}
   * are one entry.
   *
   * <p>Returns an empty list rather than failing when the provider is unreachable: a geocoder
   * assists a form field that also accepts a position entered by hand, so an outage upstream should
   * not stop someone from saving a place.
   */
  @CacheResult(cacheName = "geocode-search")
  public List<NominatimPlace> search(String normalizedQuery, String language) {
    try {
      return nominatimClient.search(normalizedQuery, "jsonv2", LIMIT, language);
    } catch (RuntimeException e) {
      LOG.warnv("Geocoding failed for \"{0}\": {1}", normalizedQuery, e.getMessage());
      return List.of();
    }
  }

  /** Lowercased and whitespace-collapsed, so near-identical queries share one cache entry. */
  public static String normalize(String query) {
    return query.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
  }
}
