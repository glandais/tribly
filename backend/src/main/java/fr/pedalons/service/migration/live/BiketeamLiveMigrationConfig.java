package fr.pedalons.service.migration.live;

import fr.pedalons.common.UrlUtils;
import fr.pedalons.service.migration.BiketeamMigrationService;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * Settings of the biketeam live migration (docs/plans/2026-09-22-biketeam-live-migration.md §8.2).
 *
 * <p>Off unless all five secrets and URLs are set. None set: the feature is disabled — every
 * endpoint answers 404, the worker idles — and one INFO line says so at startup. Only some of them:
 * startup fails, naming the missing ones, since half a configuration is always a mistake.
 *
 * <p>{@code Optional<String>} rather than {@code defaultValue = ""}: SmallRye turns an empty value
 * into null and refuses to inject it. The secrets are never logged, not even truncated.
 */
@ApplicationScoped
public class BiketeamLiveMigrationConfig {

  private static final Logger LOG = Logger.getLogger(BiketeamLiveMigrationConfig.class);

  /** HMAC-SHA256 keys shorter than this are refused: the hash output size, RFC 7518 §3.2. */
  static final int MIN_KEY_BYTES = 32;

  @ConfigProperty(name = "pedalons.biketeam.request-key")
  Optional<String> requestKey;

  @ConfigProperty(name = "pedalons.biketeam.trigger-secret")
  Optional<String> triggerSecret;

  @ConfigProperty(name = "pedalons.biketeam.export-url")
  Optional<String> exportUrl;

  @ConfigProperty(name = "pedalons.biketeam.export-secret")
  Optional<String> exportSecret;

  @ConfigProperty(name = "pedalons.biketeam.public-url")
  Optional<String> publicUrl;

  @ConfigProperty(name = "pedalons.biketeam.grant-ttl", defaultValue = "PT10M")
  Duration grantTtl;

  @ConfigProperty(name = "pedalons.biketeam.max-attempts", defaultValue = "3")
  int maxAttempts;

  @ConfigProperty(name = "pedalons.biketeam.stuck-after", defaultValue = "PT20M")
  Duration stuckAfter;

  /** How long a response body of the export may send nothing before the transfer is given up. */
  @ConfigProperty(name = "pedalons.biketeam.export-idle-timeout", defaultValue = "PT60S")
  Duration exportIdleTimeout;

  /**
   * How long one response body of the export — the snapshot, or one file — may take in all, from
   * its headers. Not bound by {@code stuck-after}: the body's reads write the heartbeat ({@link
   * JobProgressTracker#heartbeat()}).
   */
  @ConfigProperty(name = "pedalons.biketeam.export-transfer-timeout", defaultValue = "PT10M")
  Duration exportTransferTimeout;

  /**
   * How long the snapshot's response headers may take. Longer than a file's ({@link
   * BiketeamExportClient#FILE_REQUEST_TIMEOUT}): after a restart, biketeam may hash a gigabyte of a
   * big team's files before it answers.
   */
  @ConfigProperty(name = "pedalons.biketeam.export-snapshot-timeout", defaultValue = "PT5M")
  Duration exportSnapshotTimeout;

  /** Slack on top of {@link #minimumStuckAfter}: the short transactions around an element. */
  static final Duration STUCK_AFTER_MARGIN = Duration.ofMinutes(2);

  void validate(@Observes StartupEvent event) {
    List<String> missing = new ArrayList<>();
    if (blank(requestKey)) missing.add("PEDALONS_BIKETEAM_REQUEST_KEY");
    if (blank(triggerSecret)) missing.add("PEDALONS_BIKETEAM_TRIGGER_SECRET");
    if (blank(exportUrl)) missing.add("PEDALONS_BIKETEAM_EXPORT_URL");
    if (blank(exportSecret)) missing.add("PEDALONS_BIKETEAM_EXPORT_SECRET");
    if (blank(publicUrl)) missing.add("PEDALONS_BIKETEAM_PUBLIC_URL");
    if (missing.size() == 5) {
      LOG.info("Biketeam live migration disabled (no PEDALONS_BIKETEAM_* setting)");
      return;
    }
    if (!missing.isEmpty()) {
      throw new IllegalStateException(
          "Biketeam live migration half-configured, missing: " + String.join(", ", missing));
    }
    requestKeyBytes(); // throws when unusable
    UrlUtils.requireHttps(exportUrl(), "PEDALONS_BIKETEAM_EXPORT_URL");
    Duration minimum = minimumStuckAfter();
    if (stuckAfter.compareTo(minimum) < 0) {
      throw new IllegalStateException(
          "pedalons.biketeam.stuck-after ("
              + stuckAfter
              + ") must be at least "
              + minimum
              + ": a live job can go that long without a heartbeat, and would be taken for a"
              + " crashed one");
    }
    LOG.infof(
        "Biketeam live migration enabled (export %s, public site %s)", exportUrl(), publicUrl());
  }

  /**
   * The longest a live job can go without writing its heartbeat, plus {@link #STUCK_AFTER_MARGIN}.
   * The heartbeat is written before each element and, during a download, at least every {@link
   * JobProgressTracker#HEARTBEAT_EVERY} of received data; the silences are therefore:
   *
   * <ul>
   *   <li>the snapshot: its headers, one idle read, one heartbeat interval;
   *   <li>an element: a file's headers, one idle read, one heartbeat interval, then the element's
   *       transaction ({@link BiketeamMigrationService#LONGEST_ITEM_TRANSACTION_SECONDS}).
   * </ul>
   */
  Duration minimumStuckAfter() {
    Duration snapshot =
        exportSnapshotTimeout.plus(exportIdleTimeout).plus(JobProgressTracker.HEARTBEAT_EVERY);
    Duration element =
        BiketeamExportClient.FILE_REQUEST_TIMEOUT
            .plus(exportIdleTimeout)
            .plus(JobProgressTracker.HEARTBEAT_EVERY)
            .plusSeconds(BiketeamMigrationService.LONGEST_ITEM_TRANSACTION_SECONDS);
    return (snapshot.compareTo(element) > 0 ? snapshot : element).plus(STUCK_AFTER_MARGIN);
  }

  /** Whether all five settings are present. */
  public boolean isEnabled() {
    return !blank(requestKey)
        && !blank(triggerSecret)
        && !blank(exportUrl)
        && !blank(exportSecret)
        && !blank(publicUrl);
  }

  /** The decoded HMAC key biketeam signs requests with. */
  public byte[] requestKeyBytes() {
    byte[] key;
    try {
      key = Base64.getDecoder().decode(requestKey.orElse("").trim());
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("PEDALONS_BIKETEAM_REQUEST_KEY is not valid base64");
    }
    if (key.length < MIN_KEY_BYTES) {
      throw new IllegalStateException(
          "PEDALONS_BIKETEAM_REQUEST_KEY must decode to at least " + MIN_KEY_BYTES + " bytes");
    }
    return key;
  }

  public String triggerSecret() {
    return triggerSecret.orElse("");
  }

  /**
   * Biketeam's public base URL, without a trailing slash. The M2M calls cross the Internet with the
   * export secret in a header (no VPN, §3.4), so it must be {@code https://} — plain {@code http://}
   * is only accepted towards the local machine (dev, tests).
   */
  public String exportUrl() {
    return UrlUtils.stripTrailingSlash(exportUrl.orElse(""));
  }

  public String exportSecret() {
    return exportSecret.orElse("");
  }

  /** Biketeam's public {@code site.url}, without a trailing slash. */
  public String publicUrl() {
    return UrlUtils.stripTrailingSlash(publicUrl.orElse(""));
  }

  public Duration grantTtl() {
    return grantTtl;
  }

  public int maxAttempts() {
    return maxAttempts;
  }

  public Duration stuckAfter() {
    return stuckAfter;
  }

  public Duration exportIdleTimeout() {
    return exportIdleTimeout;
  }

  public Duration exportTransferTimeout() {
    return exportTransferTimeout;
  }

  public Duration exportSnapshotTimeout() {
    return exportSnapshotTimeout;
  }

  private static boolean blank(Optional<String> value) {
    return value.map(String::isBlank).orElse(true);
  }
}
