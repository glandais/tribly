package fr.pedalons.repository.trip;

import fr.pedalons.domain.trip.Trip;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamEntityType;
import fr.pedalons.repository.common.TeamEntityQueryBasic;
import fr.pedalons.repository.common.TeamEntityRepository;
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
      Long domainId,
      Long teamId,
      @Nullable Long userId,
      String slug,
      boolean includeDeleted,
      boolean platformAdmin) {
    return TeamEntityQueryBasic.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .slug(slug)
        .includeDeleted(includeDeleted)
        .platformAdmin(platformAdmin)
        .build();
  }

  @Override
  public TeamEntityQueryBasic getQueryId(
      Long domainId,
      Long teamId,
      @Nullable Long userId,
      Long id,
      boolean includeDeleted,
      boolean platformAdmin) {
    return TeamEntityQueryBasic.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .id(id)
        .includeDeleted(includeDeleted)
        .platformAdmin(platformAdmin)
        .build();
  }
}
