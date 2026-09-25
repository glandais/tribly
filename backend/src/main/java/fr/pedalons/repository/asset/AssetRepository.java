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
import org.jspecify.annotations.Nullable;

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

  /** A thumbnail asset, reduced to what it takes to locate its file and its owner. */
  public record ThumbnailAssetRow(
      Long ownerId, AssetType type, Long teamId, Long fileId, Instant createdAt) {}

  /**
   * Every thumbnail of a live entity of a live team in a domain, optionally only those drawn within [{@code
   * createdFrom}, {@code createdTo}) — a thumbnail is a fresh asset each time it is drawn, so its
   * creation date is when it was rendered. For the admin regeneration, not for any listing.
   */
  public List<ThumbnailAssetRow> findThumbnailAssets(
      Long domainId, @Nullable Instant createdFrom, @Nullable Instant createdTo) {
    StringBuilder jpql =
        new StringBuilder(
            "select a.teamEntity.id, a.type, a.team.id, a.fileId, a.createdAt from Asset a "
                + "where a.type in (:types) "
                + "and a.team.domain.id = :domainId "
                + "and a.teamEntity.deleted = false "
                + "and a.team.deleted = false");
    if (createdFrom != null) {
      jpql.append(" and a.createdAt >= :createdFrom");
    }
    if (createdTo != null) {
      jpql.append(" and a.createdAt < :createdTo");
    }
    jpql.append(" order by a.teamEntity.id");
    var query =
        getEntityManager()
            .createQuery(jpql.toString(), Object[].class)
            .setParameter("types", THUMBNAIL_TYPES)
            .setParameter("domainId", domainId);
    if (createdFrom != null) {
      query.setParameter("createdFrom", createdFrom);
    }
    if (createdTo != null) {
      query.setParameter("createdTo", createdTo);
    }
    List<ThumbnailAssetRow> result = new ArrayList<>();
    for (Object[] row : query.getResultList()) {
      result.add(
          new ThumbnailAssetRow(
              (Long) row[0], (AssetType) row[1], (Long) row[2], (Long) row[3], (Instant) row[4]));
    }
    return result;
  }

  /**
   * Live routes, rides and trips of the live teams of a domain that have something to draw but lack a light or a
   * dark thumbnail — what a failed render leaves behind.
   */
  public List<Long> findOwnersMissingThumbnails(Long domainId) {
    String twoThumbnails =
        "(select count(a) from Asset a where a.teamEntity = e and a.type in (:types)) < 2";
    List<Long> ids = new ArrayList<>();
    ids.addAll(
        getEntityManager()
            .createQuery(
                "select e.id from Route e where e.deleted = false and e.team.deleted = false"
                    + " and e.team.domain.id = :domainId"
                    + " and e.tracks is not empty and "
                    + twoThumbnails,
                Long.class)
            .setParameter("domainId", domainId)
            .setParameter(
                "types", List.of(AssetType.ROUTE_THUMBNAIL_LIGHT, AssetType.ROUTE_THUMBNAIL_DARK))
            .getResultList());
    ids.addAll(
        getEntityManager()
            .createQuery(
                "select e.id from Ride e where e.deleted = false and e.team.deleted = false"
                    + " and e.team.domain.id = :domainId"
                    + " and (e.route is not null or exists"
                    + " (select g from RideGroup g where g.ride = e and g.route is not null)) and "
                    + twoThumbnails,
                Long.class)
            .setParameter("domainId", domainId)
            .setParameter(
                "types", List.of(AssetType.RIDE_THUMBNAIL_LIGHT, AssetType.RIDE_THUMBNAIL_DARK))
            .getResultList());
    ids.addAll(
        getEntityManager()
            .createQuery(
                "select e.id from Trip e where e.deleted = false and e.team.deleted = false"
                    + " and e.team.domain.id = :domainId"
                    + " and (e.route is not null or exists (select s from TripStage s"
                    + " where s.trip = e and s.deleted = false and s.route is not null)) and "
                    + twoThumbnails,
                Long.class)
            .setParameter("domainId", domainId)
            .setParameter(
                "types", List.of(AssetType.TRIP_THUMBNAIL_LIGHT, AssetType.TRIP_THUMBNAIL_DARK))
            .getResultList());
    return ids;
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
