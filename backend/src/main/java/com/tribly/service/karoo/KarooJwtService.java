package com.tribly.service.karoo;

import com.tribly.common.TsidUtils;
import com.tribly.domain.user.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * JWT service for Karoo device OAuth tokens with longer expiry times suitable for device
 * connectivity.
 */
@ApplicationScoped
public class KarooJwtService {

  @ConfigProperty(name = "mp.jwt.verify.issuer")
  String issuer;

  @ConfigProperty(name = "tribly.karoo.jwt.access-token-expiry-minutes", defaultValue = "60")
  int accessTokenExpiryMinutes;

  @ConfigProperty(name = "tribly.karoo.jwt.refresh-token-expiry-days", defaultValue = "90")
  int refreshTokenExpiryDays;

  public String generateAccessToken(User user) {
    return Jwt.issuer(issuer)
        .subject(user.getEmail())
        .claim("email", user.getEmail())
        .claim("userId", TsidUtils.toString(user.getId()))
        .claim("domainId", TsidUtils.toString(user.getDomain().getId()))
        .claim("displayName", user.getDisplayName())
        .claim("client", "karoo")
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
