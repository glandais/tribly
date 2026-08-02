package fr.pedalons.service.security;

import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.service.auth.RefreshTokenCookieFactory;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.Nullable;

/**
 * Rejects cross-site state-changing requests that would be authenticated by the {@code
 * refresh_token} cookie alone.
 *
 * <p>{@code SameSite=Strict} used to be the project's only CSRF defence. The cookie is now {@code
 * Lax} so the SSR server can read it on a document request arriving from an external link (see
 * {@link RefreshTokenCookieFactory}), which re-opens two surfaces: the cookie-driven {@code
 * @PermitAll} endpoints {@code POST /api/auth/refresh} and {@code POST /api/auth/logout}, and
 * anything else that authenticates through {@link
 * PedalonsQueryContext#getUserFromRefreshTokenCookie}. This filter is the single choke point that
 * keeps that class of request unreachable from another origin.
 *
 * <p>Requests carrying an {@code Authorization: Bearer} token are left alone — an attacker's page
 * cannot set that header, so they are not CSRF-able. Requests carrying no session cookie are left
 * alone too: there is no ambient authority to abuse.
 *
 * <p>Non-browser callers (the mobile app, the SSR server) send neither {@code Sec-Fetch-Site} nor
 * {@code Origin} and are therefore allowed. That is not a hole: forging those requests requires an
 * attacker who can already make arbitrary HTTP calls with the victim's cookie, which no web page
 * can do.
 */
@Provider
@Priority(Priorities.AUTHENTICATION - 100)
public class CrossSiteRequestFilter implements ContainerRequestFilter {

  private static final Set<String> UNSAFE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

  @ConfigProperty(name = "quarkus.http.cors.origins")
  Optional<List<String>> corsOrigins;

  @Override
  public void filter(ContainerRequestContext ctx) {
    if (!UNSAFE_METHODS.contains(ctx.getMethod().toUpperCase(Locale.ROOT))) {
      return;
    }
    if (hasBearerToken(ctx)) {
      return;
    }
    if (!ctx.getCookies().containsKey(RefreshTokenCookieFactory.REFRESH_TOKEN_COOKIE)) {
      return;
    }
    if (isSameSite(ctx)) {
      return;
    }
    ctx.abortWith(
        Response.status(Response.Status.FORBIDDEN)
            .entity(ErrorResponse.forbidden())
            .type(MediaType.APPLICATION_JSON)
            .build());
  }

  private boolean hasBearerToken(ContainerRequestContext ctx) {
    String authorization = ctx.getHeaderString(HttpHeaders.AUTHORIZATION);
    return authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7);
  }

  private boolean isSameSite(ContainerRequestContext ctx) {
    // Sec-Fetch-Site is set by the browser itself and cannot be forged by a page: trust it when
    // present. "none" means a user-initiated navigation (typed URL, bookmark) — not an attack.
    String fetchSite = ctx.getHeaderString("Sec-Fetch-Site");
    if (fetchSite != null && !fetchSite.isBlank()) {
      return !"cross-site".equalsIgnoreCase(fetchSite.trim());
    }

    String origin = ctx.getHeaderString("Origin");
    if (origin == null || origin.isBlank()) {
      // No browser fingerprint at all — a non-browser client. See the class javadoc.
      return true;
    }
    // A literal "null" origin is an opaque one (sandboxed iframe, data: document): never trusted.
    return !"null".equalsIgnoreCase(origin.trim()) && isAllowedOrigin(origin.trim(), ctx);
  }

  private boolean isAllowedOrigin(String origin, ContainerRequestContext ctx) {
    if (corsOrigins.isPresent() && corsOrigins.get().stream().anyMatch(origin::equalsIgnoreCase)) {
      return true;
    }
    String originHost = hostOf(origin);
    return originHost != null && originHost.equalsIgnoreCase(requestHost(ctx));
  }

  /** The tenant host, resolved the same way {@link DomainResolver} resolves it. */
  private @Nullable String requestHost(ContainerRequestContext ctx) {
    String forwarded = ctx.getHeaderString("X-Forwarded-Host");
    if (forwarded != null && !forwarded.isBlank()) {
      return stripPort(forwarded.split(",")[0].trim());
    }
    String host = ctx.getHeaderString(HttpHeaders.HOST);
    return host == null || host.isBlank() ? null : stripPort(host.trim());
  }

  private @Nullable String hostOf(String origin) {
    try {
      return URI.create(origin).getHost();
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private String stripPort(String host) {
    int colon = host.lastIndexOf(':');
    return colon > 0 && host.indexOf(']') < colon ? host.substring(0, colon) : host;
  }
}
