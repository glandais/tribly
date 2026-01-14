package com.tribly.service.security;

import com.tribly.common.exception.ForbiddenException;
import com.tribly.domain.auth.AuthSession;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.enums.TeamRole;
import com.tribly.repository.auth.AuthSessionRepository;
import com.tribly.repository.team.UserTeamRepository;
import com.tribly.repository.user.UserRepository;
import com.tribly.service.team.TeamService;
import com.tribly.service.user.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jspecify.annotations.Nullable;

@RequestScoped
public class TriblyQueryContext {

  private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

  @Inject SecurityIdentity identity;

  @Inject RoutingContext routingContext;

  @Inject AuthSessionRepository authSessionRepository;

  @Nullable User user;

  boolean initialized = false;

  @Inject UserService userService;

  @Inject TeamService teamService;

  @Inject UserRepository userRepository;

  @Inject UserTeamRepository userTeamRepository;

  // for test
  public void setUserForTest(@Nullable User user) {
    this.initialized = true;
    this.user = user;
  }

  public User getUser() {
    User user = getUserNullable();
    if (user == null) {
      throw new ForbiddenException();
    }
    return user;
  }

  @Nullable
  public Long getUserIdNullable() {
    init();
    if (user == null) {
      return null;
    }
    return user.getId();
  }

  public Long getUserId() {
    init();
    if (user == null) {
      throw new ForbiddenException();
    }
    return user.getId();
  }

  public @Nullable User getUserNullable() {
    init();
    if (user != null) {
      return userRepository.findActiveById(user.getId()).orElse(null);
    } else {
      return null;
    }
  }

  void init() {
    if (!initialized) {
      doInit();
      initialized = true;
    }
  }

  void doInit() {
    if (identity.getPrincipal() instanceof JsonWebToken jwt) {
      String email = jwt.getClaim("email");
      // Lookup user - do NOT create/update
      user = userService.lookupUserByEmail(email).orElse(null);
    } else {
      // Fallback to cookie-based auth for browser direct requests (downloads, images)
      user = getUserFromRefreshTokenCookie();
    }
  }

  @Nullable
  private User getUserFromRefreshTokenCookie() {
    try {
      io.vertx.core.http.Cookie cookie = routingContext.request().getCookie(REFRESH_TOKEN_COOKIE);
      if (cookie == null) {
        return null;
      }
      String refreshToken = cookie.getValue();
      if (refreshToken == null || refreshToken.isBlank()) {
        return null;
      }
      String tokenHash = hashToken(refreshToken);
      return authSessionRepository
          .findByRefreshTokenHash(tokenHash)
          .filter(AuthSession::isValid)
          .map(AuthSession::getUser)
          .orElse(null);
    } catch (Exception e) {
      return null;
    }
  }

  private String hashToken(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 not available", e);
    }
  }

  @Nullable
  public <T> T getParam(List<Object> params, int i) {
    return (T) params.get(i);
  }

  public Context getContext(List<Object> params) {
    String teamSlug = getParam(params, 0);
    return getContext(teamSlug);
  }

  public Context getContext(@Nullable String teamSlug) {
    Team team = null;
    if (teamSlug != null) {
      team = teamService.getTeam(teamSlug);
    }
    return getContext(team);
  }

  public Context getContext(@Nullable Team team) {
    init();
    User user = getUserNullable();
    TeamRole teamRole = null;
    if (user != null && team != null) {
      teamRole =
          userTeamRepository
              .findByUserAndTeam(user.getId(), team.getId())
              .map(UserTeam::getRole)
              .orElse(null);
    }
    return new Context(team, user, teamRole);
  }
}
