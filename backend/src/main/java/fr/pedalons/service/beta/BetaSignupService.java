package fr.pedalons.service.beta;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.beta.BetaSignup;
import fr.pedalons.dto.beta.BetaSignupDto;
import fr.pedalons.dto.beta.BetaSignupRequest;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.repository.beta.BetaSignupRepository;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.annotation.Public;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BetaSignupService {

  @Inject BetaSignupRepository betaSignupRepository;

  @Inject DomainResolver domainResolver;

  /**
   * Records interest in the beta programs, or does nothing if this email already signed up.
   *
   * <p>Idempotent on purpose: a form that returns "already registered" to an anonymous submitter
   * would confirm to anyone probing it that an email is on the list. The response is the same
   * either way, and so is the outcome for the sender.
   */
  @Transactional
  @Public
  public void signup(BetaSignupRequest request) {
    String email = request.email().trim();
    if (betaSignupRepository.existsByEmail(email)) {
      return;
    }
    betaSignupRepository.persist(new BetaSignup(email, domainResolver.getDomainId()));
  }

  public PedalonsPage<BetaSignupDto> listSignups(int page, int size) {
    PedalonsPage<BetaSignup> result = betaSignupRepository.findPage(page, size);
    return new PedalonsPage<>(result.items().stream().map(this::toDto).toList(), result.total());
  }

  private BetaSignupDto toDto(BetaSignup signup) {
    return new BetaSignupDto(
        TsidUtils.toString(signup.getId()), signup.getEmail(), signup.getCreatedAt());
  }
}
