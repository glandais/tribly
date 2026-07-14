package fr.pedalons.repository.social;

import fr.pedalons.domain.social.UserSocialIdentity;
import fr.pedalons.enums.SocialProvider;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserSocialIdentityRepository implements PanacheRepository<UserSocialIdentity> {

  public Optional<UserSocialIdentity> findByProviderAndExternalId(
      Long domainId, SocialProvider provider, String externalUserId) {
    return find(
            "domainId = ?1 and provider = ?2 and externalUserId = ?3",
            domainId,
            provider,
            externalUserId)
        .firstResultOptional();
  }

  public List<UserSocialIdentity> findByUser(Long userId) {
    return find("user.id = ?1", userId).list();
  }

  public Optional<UserSocialIdentity> findByUserAndProvider(Long userId, SocialProvider provider) {
    return find("user.id = ?1 and provider = ?2", userId, provider).firstResultOptional();
  }
}
