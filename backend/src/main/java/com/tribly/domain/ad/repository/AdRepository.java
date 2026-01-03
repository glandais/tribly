package com.tribly.domain.ad.repository;

import com.tribly.domain.ad.Ad;
import com.tribly.domain.common.query.TriblyQuery;
import com.tribly.domain.common.repository.TeamEntityRepository;
import com.tribly.enums.AdType;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class AdRepository implements TeamEntityRepository<Ad, AdQuery> {

  @Override
  public String getTypeName() {
    return "Ad";
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
