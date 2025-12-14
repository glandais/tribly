package com.tribly.infrastructure.security;

import com.tribly.domain.user.User;
import com.tribly.service.auth.UserSyncService;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

/**
 * Security identity augmentor that syncs Keycloak users to the local database
 * and enriches the SecurityIdentity with user attributes.
 */
@ApplicationScoped
public class UserSecurityIdentityAugmentor implements SecurityIdentityAugmentor {

    private static final Logger LOG = Logger.getLogger(UserSecurityIdentityAugmentor.class);

    @Inject
    UserSyncService userSyncService;

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
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

                String preferredUsername = jwt.getClaim("preferred_username");

                // Get display name from various possible claims
                String displayName = jwt.getClaim("name");
                if (displayName == null || displayName.isBlank()) {
                    String givenName = jwt.getClaim("given_name");
                    String familyName = jwt.getClaim("family_name");
                    if (givenName != null || familyName != null) {
                        displayName = ((givenName != null ? givenName : "") + " " +
                                (familyName != null ? familyName : "")).trim();
                    }
                }
                if (displayName == null || displayName.isBlank()) {
                    displayName = preferredUsername != null ? preferredUsername : email;
                }

                // Sync user to database
                User user = userSyncService.syncUser(
                        email,
                        displayName
                );

                // Build augmented identity with user attributes
                return QuarkusSecurityIdentity.builder(identity)
                        .addAttribute("userId", user.getId())
                        .addAttribute("email", user.getEmail())
                        .addAttribute("displayName", user.getDisplayName())
                        .addAttribute("user", user)
                        .build();
            } else {
                LOG.warnv("augmentIdentity: Principal is not a JWT, cannot augment. PrincipalClass={0}",
                        identity.getPrincipal().getClass().getName());
            }
        } catch (Exception e) {
            LOG.error("Error augmenting security identity from Keycloak", e);
        }

        return identity;
    }
}
