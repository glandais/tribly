package com.tribly.domain.common.repository;

import com.tribly.domain.common.TeamEntity;
import java.util.Optional;

public interface EntityWithSlugRepository<T extends TeamEntity> extends BaseRepository<T> {

  Class<T> getEntityClass();

  default boolean existsByTeamAndSlug(Long teamId, String slug) {
    return count("team.id = ?1 and slug = ?2 and TYPE(this) = ?3", teamId, slug, getEntityClass())
        > 0;
  }

  default Optional<T> findByTeamAndSlug(Long teamId, String slug) {
    return find(
            "team.id = ?1 and slug = ?2 and deleted = false and TYPE(this) = ?3",
            teamId,
            slug,
            getEntityClass())
        .firstResultOptional();
  }
}
