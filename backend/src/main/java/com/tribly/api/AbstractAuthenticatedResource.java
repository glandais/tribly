package com.tribly.api;

import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.team.TeamService;
import com.tribly.service.user.UserService;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

/**
 * Abstract base class for REST resources that need authenticated user information.
 *
 * <p>Provides robust user identification that handles the Quarkus SecurityIdentityAugmentor
 * race condition (related to Quarkus bug #44990) where the augmented identity might not
 * be fully propagated to {@code @PermitAll} endpoints.
 *
 * <p>Usage: Extend this class and use {@link #getCurrentUser()} or
 * {@link #getCurrentUserOrNull()} to get the authenticated user's ID.
 */
public abstract class AbstractAuthenticatedResource {

  @Inject protected TeamService teamService;

  @Inject protected UserService userService;

  @Inject protected UserRepository userRepository;

  @Inject protected CurrentIdentityAssociation currentIdentityAssociation;

  /**
   * Gets the current authenticated user's ID.
   *
   * @return the user ID
   * @throws NotAuthorizedException if no valid authentication is present
   * @throws BusinessException with USER_NOT_SYNCED if authenticated but user not synced
   */
  protected User getCurrentUser() {
    SecurityIdentity identity =
        currentIdentityAssociation.getDeferredIdentity().await().indefinitely();

    if (identity.isAnonymous()) {
      throw new NotAuthorizedException("No valid token");
    }

    return getUser(identity).orElseThrow(() -> BusinessException.forbidden(""));
  }

  private Optional<User> getUser(SecurityIdentity identity) {
    Long userId = identity.getAttribute("userId");
    if (userId == null) {
      throw BusinessException.forbidden(
          "User profile not synchronized. Please call /api/users/me first.", "USER_NOT_SYNCED");
    }
    return userRepository.findActiveById(userId);
  }

  /**
   * Gets the current user's ID, returning null if not available.
   *
   * <p>Use this method for endpoints that support both authenticated and anonymous access
   * (e.g., {@code @PermitAll} endpoints), or when the caller needs to handle
   * missing user gracefully (e.g., /me endpoint which handles sync).
   *
   * @return the user ID, or null if anonymous or not synced
   */
  @Nullable
  protected User getCurrentUserOrNull() {
    SecurityIdentity identity =
        currentIdentityAssociation.getDeferredIdentity().await().indefinitely();
    if (identity.isAnonymous()) {
      return null;
    }
    return getUser(identity).orElse(null);
  }
}
