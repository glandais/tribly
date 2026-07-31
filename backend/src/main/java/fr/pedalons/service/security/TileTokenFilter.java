package fr.pedalons.service.security;

import fr.pedalons.service.security.annotation.TileTokenAuth;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;

/**
 * Turns a valid {@code ?t=} tile token into claims that {@link PedalonsQueryContext} can pick up.
 *
 * <p>Bound to the tile endpoints only, through {@link
 * fr.pedalons.service.security.annotation.TileToken}.
 */
@Provider
@TileTokenAuth
@Priority(Priorities.AUTHENTICATION)
public class TileTokenFilter implements ContainerRequestFilter {

  /** The query parameter carrying the token. Short because it repeats on every tile URL. */
  static final String PARAM = "t";

  @Inject TileTokenService tileTokenService;

  @Inject TileTokenContext tileTokenContext;

  @Override
  public void filter(ContainerRequestContext requestContext) {
    String token = requestContext.getUriInfo().getQueryParameters().getFirst(PARAM);

    // No token: nothing changes. The browser (session cookie) and the anonymous visitor take
    // exactly the path they took before this filter existed — which is what makes this
    // non-regressive by construction rather than by testing.
    if (token == null || token.isBlank()) {
      return;
    }

    // A token that is present but invalid or expired is an error, never a silent downgrade to the
    // anonymous tile: that tile would go out with `max-age=300` and be cached as if it were the
    // truth, so the user would read "my team's routes have vanished" with no way to repair it for
    // five minutes. A 401 is not cached, so the area fixes itself on the next camera move once the
    // client has renewed.
    TileTokenClaims claims =
        tileTokenService
            .verify(token, Instant.now())
            .orElseThrow(() -> new NotAuthorizedException("tile-token"));

    tileTokenContext.accept(claims);
  }
}
