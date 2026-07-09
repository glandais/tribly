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

  public BiketeamMigrationMap upsert(String entityType, String biketeamId, long triblyId) {
    Optional<BiketeamMigrationMap> existing = findByBiketeamId(entityType, biketeamId);
    if (existing.isPresent()) {
      BiketeamMigrationMap row = existing.get();
      row.setTriblyId(triblyId);
      row.touch();
      persist(row);
      return row;
    }
    BiketeamMigrationMap row = new BiketeamMigrationMap(entityType, biketeamId, triblyId);
    persist(row);
    return row;
  }
}
