package fr.pedalons.repository.asset;

import fr.pedalons.domain.asset.Asset;
import fr.pedalons.enums.AssetType;
import fr.pedalons.enums.Visibility;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class AssetRepository implements PanacheRepository<Asset> {

  /**
   * Everything needed to build a thumbnail URL, without loading the {@link Asset} nor walking its
   * associations.
   *
   * @param teamEntityId the ride, trip stage, trip or route the thumbnail belongs to
   */
  public record ThumbnailRow(
      Long teamEntityId, AssetType type, Long assetId, String teamSlug, Visibility visibility) {}

  public List<Asset> findOrphanedAssets(Instant olderThan) {
    return list("teamEntity is null and updatedAt < ?1", olderThan);
  }

  /**
   * The thumbnail assets of a whole set of entities, in one query.
   *
   * <p>Reading {@code teamEntity.getAssets()} to find a thumbnail costs one collection load per
   * entity and hydrates every asset of that entity — GPX files, gallery images, attachments —
   * just to keep at most two of them. Over a calendar window that is a couple of hundred round
   * trips. Here the whole window costs one statement, and nothing enters the persistence context:
   * the four scalars the URL needs are projected directly.
   *
   * <p><b>Tenancy:</b> {@code assets} is not a {@code TeamEntity} table, so no visibility clause
   * applies to it automatically. The {@code domainId} predicate below is therefore not redundant
   * belt-and-braces — it is the only thing keeping an id from another domain, should one ever reach
   * this method, from resolving to a URL.
   *
   * @param teamEntityIds ids already vetted by a {@code PedalonsQuery}; an empty set costs no query
   */
  public List<ThumbnailRow> findThumbnails(Long domainId, Collection<Long> teamEntityIds) {
    if (teamEntityIds.isEmpty()) {
      return List.of();
    }
    List<Object[]> rows =
        getEntityManager()
            .createQuery(
                "select a.teamEntity.id, a.type, a.id, a.team.slug, a.teamEntity.visibility "
                    + "from Asset a "
                    + "where a.teamEntity.id in (:ids) "
                    + "and a.type in (:types) "
                    + "and a.team.domain.id = :domainId",
                Object[].class)
            .setParameter("ids", teamEntityIds)
            .setParameter("types", THUMBNAIL_TYPES)
            .setParameter("domainId", domainId)
            .getResultList();
    List<ThumbnailRow> thumbnails = new ArrayList<>(rows.size());
    for (Object[] row : rows) {
      thumbnails.add(
          new ThumbnailRow(
              (Long) row[0],
              (AssetType) row[1],
              (Long) row[2],
              (String) row[3],
              (Visibility) row[4]));
    }
    return thumbnails;
  }

  private static final List<AssetType> THUMBNAIL_TYPES =
      List.of(
          AssetType.RIDE_THUMBNAIL_LIGHT,
          AssetType.RIDE_THUMBNAIL_DARK,
          AssetType.TRIP_THUMBNAIL_LIGHT,
          AssetType.TRIP_THUMBNAIL_DARK,
          AssetType.ROUTE_THUMBNAIL_LIGHT,
          AssetType.ROUTE_THUMBNAIL_DARK);

  /**
   * Every asset a user uploaded, for the GDPR data export. Route GPX and FIT files are ordinary
   * assets, so they come back here too, distinguished only by their type.
   *
   * <p>Backed by {@code idx_assets_created_by} (V27) — without it this is a sequential scan of the
   * largest table in the schema.
   */
  public List<Asset> findByCreator(Long domainId, Long userId) {
    return list("createdBy.id = ?2 and team.domain.id = ?1 order by createdAt", domainId, userId);
  }
}
