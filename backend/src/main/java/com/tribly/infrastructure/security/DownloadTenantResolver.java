package com.tribly.infrastructure.security;

import io.quarkus.oidc.TenantResolver;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class DownloadTenantResolver implements TenantResolver {

  @Override
  @Nullable
  public String resolve(RoutingContext context) {
    String path = context.request().path();

    if (path != null && path.startsWith("/api/download/team/")) {
      // Check for Bearer token first - use default tenant (service) for API clients
      String authHeader = context.request().getHeader("Authorization");
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        return "default"; // Default tenant handles Bearer tokens
      }
      // No Bearer token - use download tenant (web-app) for cookie-based auth
      return "download";
    }

    // Everything else uses default tenant (bearer token auth)
    return null;
  }
}
