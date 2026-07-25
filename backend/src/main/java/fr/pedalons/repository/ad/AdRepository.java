package fr.pedalons.repository.ad;

import fr.pedalons.domain.ad.Ad;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamEntityType;
import fr.pedalons.repository.common.TeamEntityRepository;
import fr.pedalons.repository.query.PedalonsQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AdRepository implements TeamEntityRepository<Ad, AdQuery> {

  @Override
  public AdQuery getQuerySlug(
      Long domainId,
      Long teamId,
      @Nullable Long userId,
      String slug,
      boolean includeDeleted,
      boolean platformAdmin) {
    return AdQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .slug(slug)
        .includeDeleted(includeDeleted)
        .platformAdmin(platformAdmin)
        .build();
  }

  @Override
  public AdQuery getQueryId(
      Long domainId,
      Long teamId,
      @Nullable Long userId,
      Long id,
      boolean includeDeleted,
      boolean platformAdmin) {
    return AdQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .id(id)
        .includeDeleted(includeDeleted)
        .platformAdmin(platformAdmin)
        .build();
  }

  @Override
  public TeamEntityType getEntityType() {
    return TeamEntityType.AD;
  }

  @Override
  public EntityType getAllEntityType() {
    return EntityType.AD;
  }

  @Override
  public PedalonsQuery andSpecific(PedalonsQuery pedalonsQuery, AdQuery query) {
    AdType adType = query.adType();
    if (adType != null) {
      pedalonsQuery = pedalonsQuery.and("te.adType = :adType", Map.of("adType", adType));
    }
    return pedalonsQuery;
  }

  /** Ads a user posted, for the GDPR data export. */
  public List<Ad> findByCreator(Long domainId, Long userId) {
    return list("createdBy.id = ?2 and team.domain.id = ?1 order by createdAt", domainId, userId);
  }
}
