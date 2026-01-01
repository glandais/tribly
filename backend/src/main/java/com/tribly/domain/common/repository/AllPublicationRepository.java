package com.tribly.domain.common.repository;

import com.tribly.domain.common.Publication;
import com.tribly.enums.Status;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class AllPublicationRepository
    implements TeamEntityRepository<Publication, TeamEntityQueryBasic> {
  @Override
  public String getTypeName() {
    return "Publication";
  }

  /**
   * Find publications that should be auto-published (DRAFT status with publishAt in the past).
   */
  public List<Publication> findPublicationsToAutoPublish() {
    return find(
            "status = ?1 and publishAt is not null and publishAt <= ?2 and deleted = false",
            Status.DRAFT,
            Instant.now())
        .list();
  }
}
