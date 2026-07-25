package fr.pedalons.service.security;

import static fr.pedalons.common.TokenUtils.hashToken;

import fr.pedalons.common.exception.ForbiddenException;
import fr.pedalons.domain.auth.AuthSession;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.team.UserTeam;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.repository.auth.AuthSessionRepository;
import fr.pedalons.repository.platform.DomainRepository;
import fr.pedalons.repository.team.UserTeamRepository;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.team.TeamService;
import fr.pedalons.service.user.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.util.List;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jspecify.annotations.Nullable;

@RequestScoped
public class PedalonsQueryContext {

  private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

  @Inject SecurityIdentity identity;

  @Inject RoutingContext routingContext;

  @Inject AuthSessionRepository authSessionRepository;

  @Inject DomainResolver domainResolver;

  @Inject DomainRepository domainRepository;

  @Nullable User user;

  boolean initialized = false;

  /** Memoized result of {@link #getUserNullable()} — see the javadoc there. */
  @Nullable User activeUser;

  boolean activeUserResolved = false;

  @Inject UserService userService;

  @Inject TeamService teamService;

  @Inject UserRepository userRepository;

  @Inject UserTeamRepository userTeamRepository;

  // for test
  public void setUserForTest(@Nullable User user) {
    this.initialized = true;
    this.user = user;
    invalidateUser();
  }

  public Domain getDomain() {
    return domainResolver.getDomain();
  }

  public Long getDomainId() {
    return domainResolver.getDomainId();
  }

  public @Nullable Domain getDomainNullable() {
    return domainResolver.getDomainNullable();
  }

  public @Nullable Long getPinnedTeamIdNullable() {
    return domainResolver.getPinnedTeamIdNullable();
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
    return user != null ? user.getId() : null;
  }

  public Long getUserId() {
    init();
    if (user == null) {
      throw new ForbiddenException();
    }
    return user.getId();
  }

  /**
   * The authenticated user of this request, or {@code null}.
   *
   * <p>Resolved at most once per request. The interceptors ({@code @Logged}, {@code @Admin}, {@code
   * @CheckAccess}), the security verifier and the services each ask for the current user
   * independently — half a dozen calls on a single endpoint is normal — and every one of them used
   * to issue its own {@code User WHERE id = ? AND deleted = false} SELECT. The lookup is a pure
   * re-validation of what {@link #doInit()} already resolved (its query filters {@code deleted =
   * false} too), so the answer cannot change mid-request unless this request itself soft-deletes the
   * user — which is what {@link #invalidateUser()} is for.
   */
  public @Nullable User getUserNullable() {
    init();
    if (user == null) {
      return null;
    }
    if (!activeUserResolved) {
      activeUser = userRepository.findActiveById(user.getId()).orElse(null);
      activeUserResolved = true;
    }
    return activeUser;
  }

  /**
   * Drops the memoized current user so the next {@link #getUserNullable()} hits the database again.
   * Call this after changing whether the current user still counts as active — soft-deleting them,
   * for instance.
   */
  public void invalidateUser() {
    activeUser = null;
    activeUserResolved = false;
  }

  /**
   * God mode for the user behind the current request. Callers holding a {@link User} resolved
   * outside the request context — an ICS calendar token, for instance — must use {@link
   * User#isPlatformAdmin()} directly instead.
   */
  public boolean isPlatformAdmin() {
    User userNullable = getUserNullable();
    return userNullable != null && userNullable.isPlatformAdmin();
  }

  void init() {
    if (!initialized) {
      doInit();
      initialized = true;
    }
  }

  void doInit() {
    Domain domain = domainResolver.getDomainNullable();

    if (identity.getPrincipal() instanceof JsonWebToken jwt) {
      String email = jwt.getClaim("email");
      // For Garmin devices (or any client with domainId in JWT), use JWT's domainId
      // when HTTP headers don't resolve a domain
      if (domain == null) {
        String domainIdStr = jwt.getClaim("domainId");
        if (domainIdStr != null) {
          Long domainId = fr.pedalons.common.TsidUtils.toLong(domainIdStr);
          domain = domainRepository.findByIdOptional(domainId).orElse(null);
        }
      }
      if (domain != null) {
        // Lookup user by email AND domain - do NOT create/update
        user = userService.lookupUserByEmailAndDomain(domain.getId(), email).orElse(null);
      }
    } else if (domain != null) {
      // Fallback to cookie-based auth for browser direct requests (downloads, images)
      user = getUserFromRefreshTokenCookie(domain.getId());
    }
  }

  @Nullable
  private User getUserFromRefreshTokenCookie(Long domainId) {
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
          .filter(u -> u.getDomain().getId().equals(domainId))
          .orElse(null);
    } catch (Exception e) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
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
