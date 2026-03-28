package fr.pedalons.service.auth;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.user.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class JwtService {

  @ConfigProperty(name = "mp.jwt.verify.issuer")
  String issuer;

  @ConfigProperty(name = "pedalons.auth.jwt.expiry-minutes", defaultValue = "15")
  int accessTokenExpiryMinutes;

  public String generateAccessToken(User user) {
    return Jwt.issuer(issuer)
        .subject(user.getEmail())
        .claim("email", user.getEmail())
        .claim("userId", TsidUtils.toString(user.getId()))
        .claim("displayName", user.getDisplayName())
        .claim("domainId", TsidUtils.toString(user.getDomain().getId()))
        .groups("user")
        .expiresIn(Duration.ofMinutes(accessTokenExpiryMinutes))
        .sign();
  }

  public int getAccessTokenExpirySeconds() {
    return accessTokenExpiryMinutes * 60;
  }
}
