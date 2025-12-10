package com.tribly.infrastructure.multitenancy;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION - 10)
public class TenantFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(TenantFilter.class);
    private static final String TEAM_DOMAIN_HEADER = "X-Team-Domain";
    private static final String TEAM_SLUG_HEADER = "X-Team-Slug";

    @Inject
    TenantContext tenantContext;

    @Inject
    TenantResolver tenantResolver;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();

        if (isPublicPath(path)) {
            LOG.debugv("Skipping tenant resolution for public path: {0}", path);
            return;
        }

        String teamDomain = requestContext.getHeaderString(TEAM_DOMAIN_HEADER);
        String teamSlug = requestContext.getHeaderString(TEAM_SLUG_HEADER);
        String hostHeader = requestContext.getHeaderString("Host");

        LOG.debugv("Resolving tenant - domain header: {0}, slug header: {1}, host: {2}",
                teamDomain, teamSlug, hostHeader);

        if (teamDomain != null && !teamDomain.isBlank()) {
            tenantResolver.resolveByDomain(teamDomain)
                    .ifPresent(team -> {
                        tenantContext.setTeamId(team.id());
                        tenantContext.setTeamSlug(team.slug());
                        tenantContext.setDomain(teamDomain);
                    });
        } else if (teamSlug != null && !teamSlug.isBlank()) {
            tenantResolver.resolveBySlug(teamSlug)
                    .ifPresent(team -> {
                        tenantContext.setTeamId(team.id());
                        tenantContext.setTeamSlug(team.slug());
                    });
        } else if (hostHeader != null && !isDefaultHost(hostHeader)) {
            String domain = extractDomain(hostHeader);
            tenantResolver.resolveByDomain(domain)
                    .ifPresent(team -> {
                        tenantContext.setTeamId(team.id());
                        tenantContext.setTeamSlug(team.slug());
                        tenantContext.setDomain(domain);
                    });
        }

        LOG.debugv("Tenant resolved - teamId: {0}, slug: {1}",
                tenantContext.getTeamId(), tenantContext.getTeamSlug());
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("auth/") ||
               path.startsWith("health") ||
               path.startsWith("q/") ||
               path.equals("openapi") ||
               path.startsWith("swagger-ui");
    }

    private boolean isDefaultHost(String host) {
        return host.contains("localhost") ||
               host.contains("127.0.0.1") ||
               host.contains("tribly.app");
    }

    private String extractDomain(String hostHeader) {
        if (hostHeader.contains(":")) {
            return hostHeader.substring(0, hostHeader.indexOf(':'));
        }
        return hostHeader;
    }
}
