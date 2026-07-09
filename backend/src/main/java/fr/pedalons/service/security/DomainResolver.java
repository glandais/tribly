package fr.pedalons.service.security;

import fr.pedalons.common.exception.NotFoundException;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.platform.DomainAlias;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.repository.platform.DomainAliasRepository;
import fr.pedalons.repository.platform.DomainRepository;
import fr.pedalons.service.security.annotation.Public;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;

@RequestScoped
public class DomainResolver {

  @Inject RoutingContext routingContext;

  @Inject DomainRepository domainRepository;

  @Inject DomainAliasRepository domainAliasRepository;

  private @Nullable Domain domain;
  private @Nullable ResolvedSite site;
  private boolean initialized = false;

  @Public
  public Domain getDomain() {
    init();
    if (domain == null) {
      throw new NotFoundException(ErrorCode.DOMAIN_NOT_FOUND);
    }
    return domain;
  }

  @Public
  public Long getDomainId() {
    return getDomain().getId();
  }

  @Public
  public @Nullable Domain getDomainNullable() {
    init();
    return domain;
  }

  /**
   * The site resolved for the current request (parent domain + effective branding + optional pinned
   * team). Throws if no domain/alias resolved from the request host.
   */
  @Public
  public ResolvedSite getResolvedSite() {
    init();
    if (site == null) {
      throw new NotFoundException(ErrorCode.DOMAIN_NOT_FOUND);
    }
    return site;
  }

  @Public
  public String getEffectiveBaseUrl() {
    return getResolvedSite().effectiveBaseUrl();
  }

  @Public
  public String getEffectiveHost() {
    return getResolvedSite().effectiveHost();
  }

  @Public
  public String getEffectiveName() {
    return getResolvedSite().effectiveName();
  }

  @Public
  public @Nullable String getEffectiveAndroidFingerprints() {
    return getResolvedSite().effectiveAndroidFingerprints();
  }

  @Public
  public @Nullable Long getPinnedTeamIdNullable() {
    init();
    return site != null ? site.pinnedTeamId() : null;
  }

  private void init() {
    if (initialized) {
      return;
    }
    initialized = true;

    String host = extractHost();
    if (host == null) {
      return;
    }

    String hostname = host.split(":")[0].toLowerCase();

    Domain resolved = domainRepository.findByDomain(hostname).orElse(null);
    if (resolved != null) {
      domain = resolved;
      site = ResolvedSite.ofDomain(resolved);
      return;
    }

    DomainAlias alias = domainAliasRepository.findByHostname(hostname).orElse(null);
    if (alias != null) {
      domain = alias.getDomain();
      site = ResolvedSite.ofAlias(alias);
    }
  }

  private @Nullable String extractHost() {
    HttpServerRequest request = routingContext.request();
    // X-Forwarded-Host takes priority (reverse proxy)
    String host = request.getHeader("X-Forwarded-Host");
    if (host != null && !host.isBlank()) {
      return host.split(",")[0].trim();
    }
    // Fallback to Host header
    return request.getHeader("Host");
  }

  // For testing purposes
  public void setDomainForTest(@Nullable Domain domain) {
    this.initialized = true;
    this.domain = domain;
    this.site = domain != null ? ResolvedSite.ofDomain(domain) : null;
  }

  // For testing purposes: simulate a request arriving on an alias hostname.
  public void setAliasForTest(DomainAlias alias) {
    this.initialized = true;
    this.domain = alias.getDomain();
    this.site = ResolvedSite.ofAlias(alias);
  }
}
