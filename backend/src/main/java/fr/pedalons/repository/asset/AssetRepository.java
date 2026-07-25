package fr.pedalons.repository.asset;

import fr.pedalons.domain.asset.Asset;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class AssetRepository implements PanacheRepository<Asset> {

  public List<Asset> findOrphanedAssets(Instant olderThan) {
    return list("teamEntity is null and updatedAt < ?1", olderThan);
  }

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
