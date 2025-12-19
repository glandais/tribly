package com.tribly.api;

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
   */
  protected Long getCurrentUserId() {
    Long userId = getCurrentUserIdOrNull();
    if (userId == null) {
      throw new NotAuthorizedException("No valid token");
    }
    return userId;
  }

  /**
   * Gets the current user's ID, returning null for anonymous users.
   *
   * <p>Use this method for endpoints that support both authenticated and anonymous access
   * (e.g., {@code @PermitAll} endpoints).
   *
   * @return the user ID, or null if anonymous
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
