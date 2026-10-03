package fr.pedalons.service.device;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.user.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * JWT service for device OAuth tokens with longer expiry times suitable for device connectivity.
 * Used by both Karoo and Garmin devices.
 */
@ApplicationScoped
public class DeviceJwtService {

  /** The pairing's session id, which {@link DeviceSessionFilter} checks on every request. */
  public static final String SESSION_CLAIM = "sid";

  @ConfigProperty(name = "mp.jwt.verify.issuer")
  String issuer;

  /**
   * 15 minutes, like the site's: the token is a JWT, which nothing revokes before it expires (audit
   * M9, docs/LEDGER_*.md SEC-11). The device refreshes more often, that is all.
   */
  @ConfigProperty(name = "pedalons.device.jwt.access-token-expiry-minutes", defaultValue = "15")
  int accessTokenExpiryMinutes;

  @ConfigProperty(name = "pedalons.device.jwt.refresh-token-expiry-days", defaultValue = "90")
  int refreshTokenExpiryDays;

  /**
   * Generate an access token for a device.
   *
   * @param user The authenticated user
   * @param clientId The client identifier (e.g., "karoo", "garmin")
   * @param sessionId The session the device's pairing opened: {@link DeviceSessionFilter} refuses
   *     the token as soon as it is revoked, so unpairing does not wait for the token to expire
   * @return JWT access token
   */
  public String generateAccessToken(User user, String clientId, Long sessionId) {
    return Jwt.issuer(issuer)
        .subject(user.getEmail())
        .claim("email", user.getEmail())
        .claim("userId", TsidUtils.toString(user.getId()))
        .claim("domainId", TsidUtils.toString(user.getDomain().getId()))
        .claim("displayName", user.getDisplayName())
        .claim("client", clientId)
        .claim(SESSION_CLAIM, TsidUtils.toString(sessionId))
        .groups("user")
        .expiresIn(Duration.ofMinutes(accessTokenExpiryMinutes))
        .sign();
  }

  public int getAccessTokenExpirySeconds() {
    return accessTokenExpiryMinutes * 60;
  }

  public int getRefreshTokenExpiryDays() {
    return refreshTokenExpiryDays;
  }
}
