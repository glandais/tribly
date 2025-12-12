package com.tribly.infrastructure.security;

import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
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

import java.util.Optional;

@ApplicationScoped
public class UserSecurityIdentityAugmentor implements SecurityIdentityAugmentor {

    private static final Logger LOG = Logger.getLogger(UserSecurityIdentityAugmentor.class);

    @Inject
    UserRepository userRepository;

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
                String subject = jwt.getSubject();
                if (subject != null) {
                    Long userId = Long.parseLong(subject);
                    Optional<User> userOpt = userRepository.findActiveById(userId);

                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        return QuarkusSecurityIdentity.builder(identity)
                                .addAttribute("userId", user.getId())
                                .addAttribute("email", user.getEmail())
                                .addAttribute("displayName", user.getDisplayName())
                                .addAttribute("user", user)
                                .build();
                    } else {
                        LOG.warnv("User with id {0} not found for JWT subject", userId);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error augmenting security identity", e);
        }

        return identity;
    }
}
