package com.tribly.domain.common.repository;

import com.tribly.domain.common.Publication;
import com.tribly.enums.Status;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import java.time.Instant;
import java.util.List;

public interface EntityWithPublishAtRepository<T extends Publication> extends PanacheRepository<T> {

  /**
   * Find rides that should be auto-published (DRAFT status with publishAt in the past).
   */
  default List<T> findPublicationsToAutoPublish() {
    return find(
            "status = ?1 and publishAt is not null and publishAt <= ?2 and deleted = false",
            Status.DRAFT,
            Instant.now())
        .list();
  }
}
