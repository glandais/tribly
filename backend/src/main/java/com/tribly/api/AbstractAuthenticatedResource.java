package com.tribly.api;

import com.tribly.domain.user.User;
import com.tribly.service.security.TeamSecurityService;
import com.tribly.service.user.UserService;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

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

    @Inject
    protected UserService userService;

    @Inject
    TeamSecurityService securityService;

    @Inject
    protected CurrentIdentityAssociation currentIdentityAssociation;

    /**
     * Gets the current authenticated user's ID.
     *
     * @return the user ID
     * @throws NotAuthorizedException if no valid authentication is present
     */
    protected Long getCurrentUserId() {
        SecurityIdentity identity = getSecurityIdentity();
        Long userId = getUserIdFromIdentity(identity);
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
        SecurityIdentity identity = getSecurityIdentity();
        if (identity.isAnonymous()) {
            return null;
        }
        return getUserIdFromIdentity(identity);
    }

    /**
     * Gets the current authenticated user's entity.
     *
     * @return the User entity
     * @throws NotAuthorizedException if no valid authentication is present
     */
    protected User getCurrentUserEntity() {
        Long userId = getCurrentUserId();
        return userService.getActiveById(userId);
    }

    /**
     * Gets the SecurityIdentity, ensuring the augmented identity is fully resolved.
     *
     * <p>Uses the deferred identity to ensure the SecurityIdentityAugmentor has completed
     * its work before returning the identity.
     *
     * @return the fully augmented SecurityIdentity
     */
    private SecurityIdentity getSecurityIdentity() {
        return currentIdentityAssociation.getDeferredIdentity().await().indefinitely();
    }

    /**
     * Gets the current user's ID from the SecurityIdentity, with fallback to JWT email lookup.
     *
     * <p>This method handles the race condition where the SecurityIdentityAugmentor might not
     * have completed. If the userId attribute is not present, it falls back to looking up
     * the user by email from the JWT claims.
     *
     * @param identity the security identity to extract user ID from
     * @return the user ID, or null if not authenticated or user not found
     */
    @Nullable
    private Long getUserIdFromIdentity(SecurityIdentity identity) {
        // First try the augmented attribute
        Long userId = identity.getAttribute("userId");
        if (userId != null) {
            return userId;
        }

        // Fallback: if we have a JWT principal, look up user by email
        if (identity.getPrincipal() instanceof JsonWebToken jwt) {
            String email = jwt.getClaim("email");
            if (email != null) {
                Optional<User> user = userService.findByEmail(email);
                if (user.isPresent()) {
                    return user.get().getId();
                }
            }
        }

        return null;
    }

}
