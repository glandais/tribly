package fr.pedalons.service.security;

import fr.pedalons.common.TokenUtils;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.service.migration.live.BiketeamLiveMigrationConfig;
import fr.pedalons.service.security.annotation.BiketeamM2M;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

/**
 * Authenticates biketeam on the {@code /api/internal/biketeam-migration} endpoints with {@code
 * X-Biketeam-Migration-Secret}.
 *
 * <p>A dedicated header rather than {@code Authorization: Bearer}: smallrye-jwt would try to read a
 * bearer as a JWT and answer 401 before the resource. Feature disabled → a bare 404, as if the
 * endpoints did not exist. The comparison is constant-time over SHA-256 digests, so it leaks neither
 * the secret nor its length; the secret is never logged.
 */
@Provider
@BiketeamM2M
@Priority(Priorities.AUTHENTICATION)
public class BiketeamM2MFilter implements ContainerRequestFilter {

  public static final String SECRET_HEADER = "X-Biketeam-Migration-Secret";

  @Inject BiketeamLiveMigrationConfig config;

  @Override
  public void filter(ContainerRequestContext ctx) {
    if (!config.isEnabled()) {
      ctx.abortWith(Response.status(Response.Status.NOT_FOUND).build());
      return;
    }
    String received = ctx.getHeaderString(SECRET_HEADER);
    if (received == null || !TokenUtils.constantTimeEquals(received, config.triggerSecret())) {
      ctx.abortWith(
          Response.status(Response.Status.UNAUTHORIZED)
              .entity(ErrorResponse.unauthorized())
              .type(MediaType.APPLICATION_JSON)
              .build());
    }
  }
}
