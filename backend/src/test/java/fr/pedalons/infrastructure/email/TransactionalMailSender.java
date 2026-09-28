package fr.pedalons.infrastructure.email;

import fr.pedalons.repository.user.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Map;

/** Sends a mail from inside a transaction, as TeamInvitationService.invite does. */
@ApplicationScoped
public class TransactionalMailSender {

  @Inject EmailService emailService;
  @Inject UserRepository userRepository;

  @Transactional
  public void sendInTransaction(String to) {
    // A database read: the transaction now holds an enlisted connection, like any real caller.
    userRepository.count();
    emailService.sendEmail(
        to,
        EmailService.EMAIL_VERIFICATION,
        "fr",
        Map.of("displayName", "X", "appName", "App", "verifyUrl", "http://x"));
  }
}
