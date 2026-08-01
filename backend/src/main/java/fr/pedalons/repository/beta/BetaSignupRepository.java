package fr.pedalons.repository.beta;

import fr.pedalons.domain.beta.BetaSignup;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BetaSignupRepository implements BaseRepository<BetaSignup> {

  public boolean existsByEmail(String email) {
    return count("lower(email) = lower(?1)", email) > 0;
  }

  public PedalonsPage<BetaSignup> findPage(int page, int size) {
    return getPage(find("order by createdAt desc"), page, size);
  }
}
