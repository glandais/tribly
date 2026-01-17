package com.tribly.repository.trip;

import com.tribly.domain.trip.Trip;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamEntityType;
import com.tribly.repository.common.TeamEntityQueryBasic;
import com.tribly.repository.common.TeamEntityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class TripRepository implements TeamEntityRepository<Trip, TeamEntityQueryBasic> {
  @Override
  public TeamEntityType getEntityType() {
    return TeamEntityType.TRIP;
  }

  @Override
  public EntityType getAllEntityType() {
    return EntityType.TRIP;
  }

  @Override
  public TeamEntityQueryBasic getQuerySlug(
      Long domainId, Long teamId, @Nullable Long userId, String slug) {
    return TeamEntityQueryBasic.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .slug(slug)
        .build();
  }

  @Override
  public TeamEntityQueryBasic getQueryId(
      Long domainId, Long teamId, @Nullable Long userId, Long id) {
    return TeamEntityQueryBasic.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .id(id)
        .build();
  }
}
