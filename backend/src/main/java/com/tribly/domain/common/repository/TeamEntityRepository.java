package com.tribly.domain.common.repository;

import com.tribly.domain.common.TeamEntity;

public abstract class TeamEntityRepository<T extends TeamEntity> implements BaseRepository<T> {

  public abstract Class<T> getEntityClass();

  public boolean existsByTeamAndSlug(Long teamId, String slug) {
    return count("team.id = ?1 and slug = ?2 and TYPE(this) = ?3", teamId, slug, getEntityClass())
        > 0;
  }
}
