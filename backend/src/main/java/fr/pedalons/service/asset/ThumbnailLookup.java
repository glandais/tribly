package fr.pedalons.service.asset;

import fr.pedalons.repository.asset.AssetRepository;
import fr.pedalons.repository.asset.AssetRepository.ThumbnailRow;
import fr.pedalons.service.security.PedalonsQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
   * Best thumbnail URL per entity, light preferred over dark.
   *
   * <p>A calendar cell shows one picture, so the two variants collapse here rather than in the DTO.
   * Light wins because it is the variant every thumbnail generator produces; dark is the fallback
   * for entities that only have that one.
   *
   * @param teamEntityIds rides, trip stages and their routes, in any order, duplicates allowed
   * @return entity id → URL template (contains a {@code {size}} placeholder); entities with no
   *     thumbnail are simply absent
   */
  public Map<Long, String> forTeamEntities(Collection<Long> teamEntityIds) {
    if (teamEntityIds.isEmpty()) {
      return Map.of();
    }
    Map<Long, ThumbnailRow> best = new HashMap<>();
    for (ThumbnailRow row : assetRepository.findThumbnails(getDomainId(), teamEntityIds)) {
      best.merge(row.teamEntityId(), row, ThumbnailLookup::preferLight);
    }
    Map<Long, String> urls = new HashMap<>(best.size());
    best.forEach(
        (entityId, row) ->
            urls.put(
                entityId,
                AssetService.buildImageUrl(row.teamSlug(), row.visibility(), row.assetId())));
    return urls;
  }

  private Long getDomainId() {
    return pedalonsContext.getDomainId();
  }

  private static ThumbnailRow preferLight(ThumbnailRow current, ThumbnailRow candidate) {
    return isLight(current) ? current : candidate;
  }

  private static boolean isLight(ThumbnailRow row) {
    return row.type().name().endsWith("_LIGHT");
  }
}
