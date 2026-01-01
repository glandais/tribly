package com.tribly.infrastructure.security;

import com.tribly.domain.user.User;
import com.tribly.service.user.UserService;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import java.util.Optional;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

/**
 * Security identity augmentor that looks up existing users from the database and enriches the
 * SecurityIdentity with user attributes.
 *
 * <p>This augmentor only performs lookup, not user creation. User creation/sync happens in the
 * /api/users/me endpoint.
 */
@ApplicationScoped
public class UserSecurityIdentityAugmentor implements SecurityIdentityAugmentor {

  private static final Logger LOG = Logger.getLogger(UserSecurityIdentityAugmentor.class);

  @Inject UserService userService;

  @Override
  public Uni<SecurityIdentity> augment(
      SecurityIdentity identity, AuthenticationRequestContext context) {
    if (identity.isAnonymous()) {
      return Uni.createFrom().item(identity);
    }

    return context.runBlocking(() -> augmentIdentity(identity));
  }

  @ActivateRequestContext
  SecurityIdentity augmentIdentity(SecurityIdentity identity) {
    try {
      if (identity.getPrincipal() instanceof JsonWebToken jwt) {
        String email = jwt.getClaim("email");

        // Lookup user - do NOT create/update
        Optional<User> userOpt = userService.lookupUserByEmail(email);

        if (userOpt.isEmpty()) {
          // User not synced yet - return identity without user attributes
          // The /me endpoint will handle sync
          LOG.debugv("User not found for email {0}, identity not augmented", email);
          return identity;
        }

        User user = userOpt.get();

        // Build augmented identity with user attributes
        return QuarkusSecurityIdentity.builder(identity)
            .addAttribute("userId", user.getId())
            .addAttribute("email", user.getEmail())
            .addAttribute("displayName", user.getDisplayName())
            .addAttribute("user", user)
            .build();
      } else {
        LOG.warnv(
            "augmentIdentity: Principal is not a JWT, cannot augment. PrincipalClass={0}",
            identity.getPrincipal().getClass().getName());
      }
    } catch (Exception e) {
      LOG.error("Error augmenting security identity from Keycloak", e);
    }

    return identity;
  }
}
