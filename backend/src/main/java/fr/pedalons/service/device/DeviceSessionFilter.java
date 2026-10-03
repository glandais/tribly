package fr.pedalons.service.device;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.repository.auth.AuthSessionRepository;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Refuses a device's access token once its pairing is revoked (docs/LEDGER_*.md API-65): unpaired
 * from the profile, or by « Déconnecter tous les appareils ». Without it the JWT kept working until
 * it expired, up to 15 minutes, and the device went on as if nothing had happened.
 *
 * <p>Only tokens carrying {@link DeviceJwtService#SESSION_CLAIM} are checked — one indexed lookup
 * per device request; the site's and the app's tokens never carry it. A 401, not a 403: Karoo and
 * Garmin both take a 401 for « pair again » and drop their tokens.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class DeviceSessionFilter implements ContainerRequestFilter {

  @Inject SecurityIdentity identity;

  @Inject AuthSessionRepository authSessionRepository;

  @Override
  public void filter(ContainerRequestContext ctx) {
    if (!(identity.getPrincipal() instanceof JsonWebToken jwt)) {
      return;
    }
    String sessionId = jwt.getClaim(DeviceJwtService.SESSION_CLAIM);
    if (sessionId == null) {
      return;
    }
    if (!authSessionRepository.isLive(TsidUtils.toLong(sessionId))) {
      ctx.abortWith(
          Response.status(Response.Status.UNAUTHORIZED)
              .entity(ErrorResponse.unauthorized())
              .type(MediaType.APPLICATION_JSON)
              .build());
    }
  }
}
