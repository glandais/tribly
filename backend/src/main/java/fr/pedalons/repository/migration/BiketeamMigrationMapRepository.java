package fr.pedalons.repository.migration;

import fr.pedalons.domain.migration.BiketeamMigrationMap;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class BiketeamMigrationMapRepository
    implements PanacheRepositoryBase<BiketeamMigrationMap, BiketeamMigrationMap.Key> {

  public Optional<BiketeamMigrationMap> findByBiketeamId(String entityType, String biketeamId) {
    return find("entityType = ?1 and biketeamId = ?2", entityType, biketeamId)
        .firstResultOptional();
  }

  public @Nullable Long findTriblyId(String entityType, String biketeamId) {
    return findByBiketeamId(entityType, biketeamId)
        .map(BiketeamMigrationMap::getTriblyId)
        .orElse(null);
  }

  public @Nullable String findFingerprint(String entityType, String biketeamId) {
    return findByBiketeamId(entityType, biketeamId)
        .map(BiketeamMigrationMap::getSourceFingerprint)
        .orElse(null);
  }

  /** Leaves any recorded fingerprint alone — for rows whose source is not a file. */
  public BiketeamMigrationMap upsert(String entityType, String biketeamId, long triblyId) {
    return upsert(entityType, biketeamId, triblyId, null);
  }

  /**
   * A non-null {@code fingerprint} marks the row as fully built from that exact source file. Pass
   * null to leave the stored one untouched; there is no way to clear it, and nothing needs to.
   */
  public BiketeamMigrationMap upsert(
      String entityType, String biketeamId, long triblyId, @Nullable String fingerprint) {
    Optional<BiketeamMigrationMap> existing = findByBiketeamId(entityType, biketeamId);
    if (existing.isPresent()) {
      BiketeamMigrationMap row = existing.get();
      row.setTriblyId(triblyId);
      if (fingerprint != null) {
        row.setSourceFingerprint(fingerprint);
      }
      row.touch();
      persist(row);
      return row;
    }
    BiketeamMigrationMap row = new BiketeamMigrationMap(entityType, biketeamId, triblyId);
    row.setSourceFingerprint(fingerprint);
    persist(row);
    return row;
  }
}
