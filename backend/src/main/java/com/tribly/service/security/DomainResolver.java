package com.tribly.service.security;

import com.tribly.common.exception.NotFoundException;
import com.tribly.domain.platform.Domain;
import com.tribly.dto.error.ErrorCode;
import com.tribly.repository.platform.DomainRepository;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;

@RequestScoped
public class DomainResolver {

  @Inject RoutingContext routingContext;

  @Inject DomainRepository domainRepository;

  private @Nullable Domain domain;
  private boolean initialized = false;

  public Domain getDomain() {
    init();
    if (domain == null) {
      throw new NotFoundException(ErrorCode.DOMAIN_NOT_FOUND);
    }
    return domain;
  }

  public Long getDomainId() {
    return getDomain().getId();
  }

  public @Nullable Domain getDomainNullable() {
    init();
    return domain;
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

    String domainName = host.split(":")[0].toLowerCase();
    domain = domainRepository.findByDomain(domainName).orElse(null);
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
  }
}
