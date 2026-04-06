package fr.pedalons.repository.post;

import fr.pedalons.domain.post.Post;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamEntityType;
import fr.pedalons.repository.common.TeamEntityQueryBasic;
import fr.pedalons.repository.common.TeamEntityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class PostRepository implements TeamEntityRepository<Post, TeamEntityQueryBasic> {
  @Override
  public TeamEntityType getEntityType() {
    return TeamEntityType.POST;
  }

  @Override
  public EntityType getAllEntityType() {
    return EntityType.POST;
  }

  @Override
  public TeamEntityQueryBasic getQuerySlug(
      Long domainId, Long teamId, @Nullable Long userId, String slug, boolean includeDeleted) {
    return TeamEntityQueryBasic.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .slug(slug)
        .includeDeleted(includeDeleted)
        .build();
  }

  @Override
  public TeamEntityQueryBasic getQueryId(
      Long domainId, Long teamId, @Nullable Long userId, Long id, boolean includeDeleted) {
    return TeamEntityQueryBasic.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .id(id)
        .includeDeleted(includeDeleted)
        .build();
  }
}
