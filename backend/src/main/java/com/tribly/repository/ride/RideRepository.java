package com.tribly.repository.ride;

import com.tribly.domain.ride.Ride;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamEntityType;
import com.tribly.repository.common.TeamEntityQueryBasic;
import com.tribly.repository.common.TeamEntityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class RideRepository implements TeamEntityRepository<Ride, TeamEntityQueryBasic> {
  @Override
  public TeamEntityType getEntityType() {
    return TeamEntityType.RIDE;
  }

  @Override
  public EntityType getAllEntityType() {
    return EntityType.RIDE;
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
