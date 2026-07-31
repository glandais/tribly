package fr.pedalons.service.security;

import jakarta.enterprise.context.RequestScoped;
import org.jspecify.annotations.Nullable;

/**
 * The relay between {@link TileTokenFilter} and {@link PedalonsQueryContext}.
 *
 * <p>The writer is package-private on purpose: only the filter — which lives in this package and is
 * bound to the methods carrying {@link fr.pedalons.service.security.annotation.TileTokenAuth} — can
 * fill it in. That is what structurally forbids a tile token from identifying its bearer anywhere
 * else. Outside those endpoints the claims are always null, and the guarantee is held by the
 * compiler and by JAX-RS name binding rather than by anyone's discipline.
 */
@RequestScoped
public class TileTokenContext {

  private @Nullable TileTokenClaims claims;

  void accept(TileTokenClaims claims) {
    this.claims = claims;
  }

  public @Nullable TileTokenClaims getClaims() {
    return claims;
  }
}
