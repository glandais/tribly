package com.tribly.infrastructure.security;

import io.quarkus.oidc.TenantResolver;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class DownloadTenantResolver implements TenantResolver {

    @Override
    public String resolve(RoutingContext context) {
        String path = context.request().path();

        // Route download endpoints to download tenant only if enabled (not in test mode)
        if (path != null && path.startsWith("/api/download/")) {
            return "download";
        }

        // Everything else uses default tenant (bearer token auth)
        return null;
    }
}
