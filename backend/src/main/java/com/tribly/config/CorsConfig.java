package com.tribly.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;

@Provider
public class CorsConfig implements ContainerResponseFilter {

    @ConfigProperty(name = "quarkus.http.cors.origins")
    Optional<String> corsOrigins;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        // CORS is primarily handled by Quarkus configuration in application.properties
        // This filter adds any additional headers needed for multi-tenant scenarios

        String origin = requestContext.getHeaderString("Origin");
        if (origin != null && isAllowedOrigin(origin)) {
            responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        }
    }

    private boolean isAllowedOrigin(String origin) {
        if (corsOrigins.isEmpty()) {
            return false;
        }

        String[] allowed = corsOrigins.get().split(",");
        for (String allowedOrigin : allowed) {
            if (allowedOrigin.trim().equals(origin) || allowedOrigin.trim().equals("*")) {
                return true;
            }
        }

        return false;
    }
}
