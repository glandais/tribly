package com.tribly.repository.ad;

import com.tribly.domain.ad.Ad;
import com.tribly.enums.AdType;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamEntityType;
import com.tribly.repository.common.TeamEntityRepository;
import com.tribly.repository.query.TriblyQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AdRepository implements TeamEntityRepository<Ad, AdQuery> {

  @Override
  public AdQuery getQuerySlug(Long teamId, @Nullable Long userId, String slug) {
    return AdQuery.builder().teamIds(Set.of(teamId)).userId(userId).slug(slug).build();
  }

  @Override
  public AdQuery getQueryId(Long teamId, @Nullable Long userId, Long id) {
    return AdQuery.builder().teamIds(Set.of(teamId)).userId(userId).id(id).build();
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
  public TriblyQuery andSpecific(TriblyQuery triblyQuery, AdQuery query) {
    AdType adType = query.adType();
    if (adType != null) {
      triblyQuery = triblyQuery.and("te.adType = :adType", Map.of("adType", adType));
    }
    return triblyQuery;
  }
}
