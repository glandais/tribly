package fr.pedalons.service.asset;

import fr.pedalons.repository.asset.AssetRepository;
import fr.pedalons.repository.asset.AssetRepository.ThumbnailRow;
import fr.pedalons.service.security.PedalonsQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * Resolves thumbnail URLs for a set of entities in bulk — one query for a whole page, or for a whole
 * calendar window.
 *
 * <p>The single-entity path ({@code AssetService.getImageUrl}) is fine when there is one entity. Fed
 * a list, it becomes the classic shape: {@code entity.getAssets()} is a lazy collection, so a
 * three-month agenda over a busy team walks it once per event and hydrates every asset of every
 * ride. That is the regression this class exists to prevent; see {@link
 * AssetRepository#findThumbnails}.
 *
 * <p>Callers hand in every id that may carry the picture — for a ride, both the ride and its route —
 * and read the result back with a fallback, {@code map.get(rideId)} then {@code map.get(routeId)},
 * mirroring what {@code RideDto} does on the detail path.
 *
 * <p><b>Tenancy:</b> the domain comes from the request context and is applied in the query, so an id
 * belonging to another domain resolves to nothing rather than to a URL.
 */
@ApplicationScoped
public class ThumbnailLookup {

  @Inject AssetRepository assetRepository;

  @Inject PedalonsQueryContext pedalonsContext;

  /**
   * The two themed variants of one entity's thumbnail, either of which may be absent.
   *
   * <p>They are kept apart rather than collapsed because a client renders in a colour scheme the
   * server does not know: picking one here would hand a dark-mode calendar the light map tile.
   *
   * @param light URL template of the light variant, null when the entity has only a dark one
   * @param dark URL template of the dark variant, null when the entity has only a light one
   */
  public record ThemedThumbnail(@Nullable String light, @Nullable String dark) {

    /**
     * One of the two, for a client that renders a single picture and does not care which — light
     * first, because it is the variant every thumbnail generator produces.
     */
    public @Nullable String collapsed() {
      return light != null ? light : dark;
    }
  }

  /**
   * Both themed thumbnail variants per entity, in one query.
   *
   * @param teamEntityIds rides, trip stages and their routes, in any order, duplicates allowed
   * @return entity id → its variants (URL templates, each containing a {@code {size}} placeholder);
   *     entities with no thumbnail at all are simply absent, so a present value always carries at
   *     least one of the two
   */
  public Map<Long, ThemedThumbnail> forTeamEntities(Collection<Long> teamEntityIds) {
    if (teamEntityIds.isEmpty()) {
      return Map.of();
    }
    Map<Long, String> light = new HashMap<>();
    Map<Long, String> dark = new HashMap<>();
    for (ThumbnailRow row : assetRepository.findThumbnails(getDomainId(), teamEntityIds)) {
      String url = AssetService.buildImageUrl(row.teamSlug(), row.visibility(), row.assetId());
      (isLight(row) ? light : dark).put(row.teamEntityId(), url);
    }
    Map<Long, ThemedThumbnail> thumbnails = new HashMap<>();
    for (Long entityId : union(light.keySet(), dark.keySet())) {
      thumbnails.put(entityId, new ThemedThumbnail(light.get(entityId), dark.get(entityId)));
    }
    return thumbnails;
  }

  private static Set<Long> union(Set<Long> a, Set<Long> b) {
    Set<Long> all = new HashSet<>(a);
    all.addAll(b);
    return all;
  }

  private Long getDomainId() {
    return pedalonsContext.getDomainId();
  }

  private static boolean isLight(ThumbnailRow row) {
    return row.type().name().endsWith("_LIGHT");
  }
}
