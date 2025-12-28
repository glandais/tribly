package com.tribly.domain.common.repository;

import com.tribly.domain.common.Publication;
import com.tribly.domain.common.TeamEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AllTeamEntityRepository
    implements TeamEntityRepository<TeamEntity, TeamEntityQueryBasic> {
  @Override
  public String getTypeName() {
    return "TeamEntity";
  }
}
