package fr.pedalons.service.social;

import fr.pedalons.domain.social.UserSocialIdentity;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.SocialProvider;
import fr.pedalons.repository.social.UserSocialIdentityRepository;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.security.annotation.Public;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Locale;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Backfills {@link UserSocialIdentity} rows for accounts migrated before the identity table existed.
 * Migrated Strava accounts carry a {@code strava_<athleteId>@<placeholderDomain>} email; this reads
 * the athlete id from that local-part and creates the identity so a returning user matches by
 * identity rather than by placeholder-email parsing. Idempotent (unique constraint + existence
 * check), so it is safe to run repeatedly.
 */
@ApplicationScoped
public class SocialIdentityBackfillService {

  @Inject UserRepository userRepository;
  @Inject UserSocialIdentityRepository identityRepository;

  @ConfigProperty(name = "pedalons.social.placeholder-email-domain", defaultValue = "pedalons.fr")
  String placeholderEmailDomain;

  /** Backfill Strava identities for all placeholder accounts in a domain. Returns rows created. */
  @Public
  @Transactional
  public int backfillStravaIdentities(Long domainId) {
    List<User> users = userRepository.findPlaceholderStravaUsers(domainId, placeholderEmailDomain);
    int created = 0;
    for (User user : users) {
      String athleteId = parseAthleteId(user.getEmail());
      if (athleteId == null) {
        continue;
      }
      boolean exists =
          identityRepository
              .findByProviderAndExternalId(domainId, SocialProvider.STRAVA, athleteId)
              .isPresent();
      if (exists) {
        continue;
      }
      identityRepository.persist(
          new UserSocialIdentity(user, domainId, SocialProvider.STRAVA, athleteId));
      created++;
    }
    Log.infof(
        "Strava identity backfill for domain %d: %d placeholder users, %d identities created",
        domainId, users.size(), created);
    return created;
  }

  /** Extracts {@code <athleteId>} from a {@code strava_<athleteId>@<domain>} email. */
  private @org.jspecify.annotations.Nullable String parseAthleteId(String email) {
    String lower = email.toLowerCase(Locale.ROOT);
    String prefix = "strava_";
    int at = lower.indexOf('@');
    if (!lower.startsWith(prefix) || at <= prefix.length()) {
      return null;
    }
    String id = lower.substring(prefix.length(), at);
    return id.isBlank() ? null : id;
  }
}
