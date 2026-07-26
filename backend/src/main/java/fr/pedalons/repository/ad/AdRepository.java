package fr.pedalons.repository.ad;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

import fr.pedalons.common.CoarseLocation;
import fr.pedalons.domain.ad.Ad;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.SortDirection;
import fr.pedalons.enums.TeamEntityType;
import fr.pedalons.repository.common.TeamEntityRepository;
import fr.pedalons.repository.query.PedalonsQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AdRepository implements TeamEntityRepository<Ad, AdQuery> {

  /** Same default as the route search: a comfortable ride out and back. */
  private static final double DEFAULT_NEAR_RADIUS = 25_000;

  /** Beyond this the filter selects everything anyway, so it only buys a full scan. */
  private static final double MAX_NEAR_RADIUS = 500_000;

  @Override
  public AdQuery getQuerySlug(
      Long domainId,
      Long teamId,
      @Nullable Long userId,
      String slug,
      boolean includeDeleted,
      boolean platformAdmin) {
    return AdQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .slug(slug)
        .includeDeleted(includeDeleted)
        .platformAdmin(platformAdmin)
        .build();
  }

  @Override
  public AdQuery getQueryId(
      Long domainId,
      Long teamId,
      @Nullable Long userId,
      Long id,
      boolean includeDeleted,
      boolean platformAdmin) {
    return AdQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .id(id)
        .includeDeleted(includeDeleted)
        .platformAdmin(platformAdmin)
        .build();
  }

  @Override
  public TeamEntityType getEntityType() {
    return TeamEntityType.AD;
  }

  @Override
  public EntityType getAllEntityType() {
    return EntityType.AD;
  }

  @Override
  public PedalonsQuery andSpecific(PedalonsQuery pedalonsQuery, AdQuery query) {
    AdType adType = query.adType();
    if (adType != null) {
      pedalonsQuery = pedalonsQuery.and("te.adType = :adType", Map.of("adType", adType));
    }
    if (query.minPrice() != null) {
      pedalonsQuery =
          pedalonsQuery.and("te.price >= :minPrice", Map.of("minPrice", query.minPrice()));
    }
    if (query.maxPrice() != null) {
      pedalonsQuery =
          pedalonsQuery.and("te.price <= :maxPrice", Map.of("maxPrice", query.maxPrice()));
    }

    // Geographic proximity, same shape as RouteRepository — one point here, so no NearType.
    //
    // The column holds the seller's exact position; only the blurred one is ever published
    // (see CoarseLocation). A proximity filter reading the exact column is therefore an oracle:
    // ask "is this ad within R of C?" repeatedly and you recover what the blur was meant to hide.
    // Flooring R does not help — an attacker who cannot shrink the radius simply moves C and
    // intersects the discs, which multilaterates the true point to arbitrary precision.
    //
    // What closes it is quantising the *probe* rather than the answer: C is snapped to the same
    // grid the published point uses, and R to whole cells. Every reachable query is then one the
    // published data already answers, so the filter cannot resolve finer than what we publish.
    // Cost: results shift by under a cell — irrelevant when choosing between "a short ride" and
    // "a drive away", which is all this filter is for.
    if (query.nearLat() != null && query.nearLon() != null) {
      Point<G2D> probe = CoarseLocation.blur(point(WGS84, g(query.nearLon(), query.nearLat())));
      double radius = query.nearRadius() != null ? query.nearRadius() : DEFAULT_NEAR_RADIUS;
      // Capped: an unbounded radius neutralises the filter and still pays for a full-table
      // st_distancesphere, which is not indexable as written.
      radius = Math.min(radius, MAX_NEAR_RADIUS);
      radius = CoarseLocation.snapRadiusUp(radius);
      pedalonsQuery =
          pedalonsQuery.and(
              "st_distancesphere(te.locationGeometry, :nearPoint) <= :nearRadius",
              Map.of("nearPoint", probe, "nearRadius", radius));
    }

    if (query.sortBy() != null) {
      SortDirection dir = query.sortDir() != null ? query.sortDir() : SortDirection.DESC;
      pedalonsQuery =
          pedalonsQuery.order(query.sortBy().getField() + " " + dir.name().toLowerCase());
    }
    return pedalonsQuery;
  }

  /** Ads a user posted, for the GDPR data export. */
  public List<Ad> findByCreator(Long domainId, Long userId) {
    return list("createdBy.id = ?2 and team.domain.id = ?1 order by createdAt", domainId, userId);
  }
}
