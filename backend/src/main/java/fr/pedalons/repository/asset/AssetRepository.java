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
}
