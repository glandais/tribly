package com.tribly.domain.common.repository;

import com.tribly.domain.common.TeamEntity;
import com.tribly.enums.EntityType;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AllTeamEntityRepository
    implements TeamEntityRepository<TeamEntity, TeamEntityQueryBasic> {
  @Override
  public EntityType getEntityType() {
    return EntityType.ALL;
  }
}
