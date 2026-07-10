package fr.pedalons.repository.gpx;

import fr.pedalons.domain.gpx.GpxPreview;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class GpxPreviewRepository implements PanacheRepository<GpxPreview> {

  /**
   * Looks up a preview by its public identifier, scoped to a domain.
   *
   * <p>{@code publicId} is globally unique, so the domain filter is redundant for correctness — it
   * is there so that a leaked identifier cannot cross a tenant boundary.
   */
  public Optional<GpxPreview> findByPublicId(Long domainId, UUID publicId) {
    return find("domain.id = ?1 and publicId = ?2", domainId, publicId).firstResultOptional();
  }

  /** Previews created before the cutoff, for the retention job. */
  public List<GpxPreview> findExpired(Instant cutoff) {
    return list("createdAt < ?1", cutoff);
  }
}
