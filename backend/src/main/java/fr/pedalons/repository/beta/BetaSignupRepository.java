package fr.pedalons.repository.beta;

import fr.pedalons.domain.beta.BetaSignup;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;

@ApplicationScoped
public class BetaSignupRepository implements BaseRepository<BetaSignup> {

  public boolean existsByEmail(String email) {
    return count("lower(email) = lower(?1)", email) > 0;
  }

  /** Sign-ups older than {@code cutoff} — the retention the privacy policy announces (§6). */
  public long deleteCreatedBefore(Instant cutoff) {
    return delete("createdAt < ?1", cutoff);
  }

  public PedalonsPage<BetaSignup> findPage(int page, int size) {
    return getPage(find("order by createdAt desc"), page, size);
  }
}
