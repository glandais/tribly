package fr.pedalons.service.auth;

import fr.pedalons.common.exception.TooManyRequestsException;
import fr.pedalons.domain.auth.AuthFailure;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.enums.AuthFailureKind;
import fr.pedalons.repository.auth.AuthFailureRepository;
import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.Nullable;

/**
 * Refuses a guess at a secret once too many recent guesses failed — before paying for it (audit H5
 * and M4, docs/LEDGER_*.md SEC-4 and SEC-7).
 *
 * <p>Only failures are counted, so nobody who types their password right, or pairs the device in
 * front of them, ever meets the limit. The check comes first and a refused guess records nothing:
 * past the limit, a flood neither costs a bcrypt nor grows the table.
 *
 * <p>Keyed on what the attacker cannot vary for free — the targeted address, the signed-in account,
 * the domain — never on the client IP: the first {@code X-Forwarded-For} hop is whatever the client
 * sent ({@code forwardedHeaders.insecure} on the Traefik entry point), so a per-IP count would stop
 * only the attackers who don't bother to lie. Spreading one password over many addresses (credential
 * stuffing) is therefore not stopped here: that is the global rate limit of audit M10.
 *
 * <p>Failures are written in their own transaction: the caller's rolls back with the error it throws,
 * and would take the count with it.
 */
@ApplicationScoped
public class AuthThrottle {

  /** The subject of an anonymous pairing-code lookup: counted only in the domain's budget. */
  static final String ANONYMOUS = "-";

  @Inject AuthFailureRepository authFailureRepository;

  /**
   * Wrong passwords an address survives per window. The lock is per address whoever tries, so a
   * stranger can hold someone's password sign-in shut — only that one: the e-mailed code and the
   * passkeys stay open, and the window is short.
   */
  @ConfigProperty(name = "pedalons.auth.password.max-failures", defaultValue = "5")
  int passwordMaxFailures;

  @ConfigProperty(name = "pedalons.auth.password.failure-window-minutes", defaultValue = "15")
  int passwordWindowMinutes;

  /** Unknown pairing codes one signed-in account may submit per window. */
  @ConfigProperty(name = "pedalons.auth.device-code.max-failures-per-user", defaultValue = "5")
  int deviceCodeMaxFailuresPerUser;

  /**
   * Unknown pairing codes the whole domain may submit per window, signed in or not. With 32⁶ codes,
   * 300 guesses find a given pending code once in 3.5 million windows. The price: a flood holds
   * pairing shut for the whole domain until the window slides — a nuisance, where the per-account
   * limit alone let a crowd of throwaway accounts guess without bound.
   */
  @ConfigProperty(name = "pedalons.auth.device-code.max-failures-per-domain", defaultValue = "300")
  int deviceCodeMaxFailuresPerDomain;

  /** The life of a pairing code: a guess can only ever hit a code created in the last window. */
  @ConfigProperty(name = "pedalons.auth.device-code.failure-window-minutes", defaultValue = "10")
  int deviceCodeWindowMinutes;

  /** Throws {@code 429 LOGIN_RATE_LIMITED} once {@code email} has too many recent wrong passwords. */
  public void checkPassword(Long domainId, String email) {
    String subject = emailSubject(email);
    Duration window = Duration.ofMinutes(passwordWindowMinutes);
    long failures =
        authFailureRepository.countSince(
            domainId, AuthFailureKind.PASSWORD, subject, Instant.now().minus(window));
    if (failures >= passwordMaxFailures) {
      Log.warnf("Password sign-in throttled domain=%d subject=%s", domainId, subject);
      throw new TooManyRequestsException(ErrorCode.LOGIN_RATE_LIMITED, window.toSeconds());
    }
  }

  public void recordPasswordFailure(Long domainId, String email) {
    record(domainId, AuthFailureKind.PASSWORD, emailSubject(email));
  }

  /** A right password wipes the slate: the failures before it were the owner's typos. */
  public void clearPassword(Long domainId, String email) {
    authFailureRepository.deleteBySubject(domainId, AuthFailureKind.PASSWORD, emailSubject(email));
  }

  /**
   * Throws {@code 429 DEVICE_CODE_RATE_LIMITED} once the domain, or {@code userId} when signed in,
   * has too many recent unknown pairing codes.
   *
   * @param userId null for an anonymous lookup, which only the domain's budget bounds
   */
  public void checkDeviceCode(Long domainId, @Nullable Long userId) {
    Duration window = Duration.ofMinutes(deviceCodeWindowMinutes);
    Instant since = Instant.now().minus(window);
    boolean userOver =
        userId != null
            && authFailureRepository.countSince(
                    domainId, AuthFailureKind.DEVICE_CODE, userId.toString(), since)
                >= deviceCodeMaxFailuresPerUser;
    boolean domainOver =
        !userOver
            && authFailureRepository.countSince(domainId, AuthFailureKind.DEVICE_CODE, since)
                >= deviceCodeMaxFailuresPerDomain;
    if (userOver || domainOver) {
      Log.warnf(
          "Device pairing throttled domain=%d user=%s scope=%s",
          domainId, userId, userOver ? "user" : "domain");
      throw new TooManyRequestsException(ErrorCode.DEVICE_CODE_RATE_LIMITED, window.toSeconds());
    }
  }

  public void recordDeviceCodeFailure(Long domainId, @Nullable Long userId) {
    record(domainId, AuthFailureKind.DEVICE_CODE, userId == null ? ANONYMOUS : userId.toString());
  }

  private void record(Long domainId, AuthFailureKind kind, String subject) {
    QuarkusTransaction.requiringNew()
        .run(() -> authFailureRepository.persist(new AuthFailure(domainId, kind, subject)));
  }

  /**
   * The address as the counter knows it: case-folded, so {@code Alice@} and {@code alice@} share
   * one budget, and hashed, so the table holds no address in clear.
   */
  static String emailSubject(String email) {
    try {
      byte[] hash =
          MessageDigest.getInstance("SHA-256")
              .digest(email.strip().toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 not available", e);
    }
  }
}
