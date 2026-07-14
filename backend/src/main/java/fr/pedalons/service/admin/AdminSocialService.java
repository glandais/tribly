package fr.pedalons.service.admin;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.annotation.Admin;
import fr.pedalons.service.social.SocialIdentityBackfillService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AdminSocialService {

  @Inject SocialIdentityBackfillService backfillService;
  @Inject DomainResolver domainResolver;

  /**
   * Backfill Strava social identities for migrated placeholder accounts. Targets the given domain,
   * or the request's resolved domain when {@code domainId} is null.
   */
  @Admin
  public int backfillStravaIdentities(@Nullable String domainId) {
    Long targetDomainId =
        domainId != null ? TsidUtils.toLong(domainId) : domainResolver.getDomainId();
    return backfillService.backfillStravaIdentities(targetDomainId);
  }
}
