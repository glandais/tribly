package fr.pedalons.service.security;

import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.platform.DomainAlias;
import org.jspecify.annotations.Nullable;

/**
 * The site resolved for the current request. Always backed by a parent {@link Domain} (which owns
 * users and content). When the request arrived on a {@link DomainAlias} hostname, the effective
 * host/baseUrl/name/branding come from the alias and a pinned team is set, so the app renders in
 * single-team mode for that team — while {@code domain} stays the parent so every existing
 * domain-scoped query is unchanged.
 */
public record ResolvedSite(
    Domain domain,
    String effectiveHost,
    String effectiveBaseUrl,
    String effectiveName,
    @Nullable String effectiveAndroidFingerprints,
    @Nullable Long pinnedTeamId,
    boolean singleTeam) {

  public static ResolvedSite ofDomain(Domain domain) {
    return new ResolvedSite(
        domain,
        domain.getDomain(),
        domain.getBaseUrl(),
        domain.getName(),
        domain.getAndroidFingerprints(),
        null,
        domain.isSingleTeam());
  }

  public static ResolvedSite ofAlias(DomainAlias alias) {
    Domain parent = alias.getDomain();
    return new ResolvedSite(
        parent,
        alias.getHostname(),
        alias.getBaseUrl(),
        alias.getName(),
        alias.getAndroidFingerprints(),
        alias.getPinnedTeam().getId(),
        true);
  }

  public boolean isAlias() {
    return pinnedTeamId != null;
  }
}
