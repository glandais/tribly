package com.tribly.api;

import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.user.UserService;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import org.jspecify.annotations.Nullable;

/**
 * Abstract base class for REST resources that need authenticated user information.
 *
 * <p>Provides robust user identification that handles the Quarkus SecurityIdentityAugmentor
 * race condition (related to Quarkus bug #44990) where the augmented identity might not
 * be fully propagated to {@code @PermitAll} endpoints.
 *
 * <p>Usage: Extend this class and use {@link #getCurrentUserId()} or
 * {@link #getCurrentUserIdOrNull()} to get the authenticated user's ID.
 */
public abstract class AbstractAuthenticatedResource {

  @Inject protected UserService userService;

  @Inject protected CurrentIdentityAssociation currentIdentityAssociation;

  /**
   * Gets the current authenticated user's ID.
   *
   * @return the user ID
   * @throws NotAuthorizedException if no valid authentication is present
   * @throws BusinessException with USER_NOT_SYNCED if authenticated but user not synced
   */
  protected Long getCurrentUserId() {
    SecurityIdentity identity =
        currentIdentityAssociation.getDeferredIdentity().await().indefinitely();

    if (identity.isAnonymous()) {
      throw new NotAuthorizedException("No valid token");
    }

    Long userId = identity.getAttribute("userId");
    if (userId == null) {
      throw new BusinessException(
          "User profile not synchronized. Please call /api/users/me first.",
          BusinessException.ErrorType.FORBIDDEN,
          "USER_NOT_SYNCED");
    }
    return userId;
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
  protected Long getCurrentUserIdOrNull() {
    SecurityIdentity identity =
        currentIdentityAssociation.getDeferredIdentity().await().indefinitely();
    if (identity.isAnonymous()) {
      return null;
    }
    return identity.getAttribute("userId");
  }
}
