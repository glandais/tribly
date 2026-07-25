package fr.pedalons.service.user;

import fr.pedalons.common.TokenUtils;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.NotFoundException;
import fr.pedalons.common.exception.TooManyRequestsException;
import fr.pedalons.domain.user.User;
import fr.pedalons.domain.user.UserExport;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.users.response.DownloadableExport;
import fr.pedalons.dto.users.response.UserExportDto;
import fr.pedalons.enums.UserExportStatus;
import fr.pedalons.infrastructure.storage.StorageService;
import fr.pedalons.repository.user.UserExportRepository;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.ResolvedSite;
import fr.pedalons.service.security.annotation.Logged;
import fr.pedalons.service.security.annotation.Public;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

/**
 * Self-service GDPR data export (DSAR).
 *
 * <p>The {@code user_exports} row is the job queue. A request inserts it as {@code PENDING} and
 * returns immediately; {@code UserExportScheduler} claims it, builds the archive outside any
 * transaction, stores it and emails a link.
 *
 * <p>Rate limiting is derived from the export table rather than a column on {@code users}: that
 * table is read on every authenticated request, and the export row already answers the richer
 * question — not just "when was the last one" but "is one running right now".
 */
@ApplicationScoped
public class UserExportService {

  private static final Logger LOG = Logger.getLogger(UserExportService.class);

  private static final String KEY_PREFIX = "exports/";
  private static final String DEFAULT_LANGUAGE = "fr";
  private static final DateTimeFormatter FILE_DATE =
      DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);

  @Inject UserExportRepository userExportRepository;
  @Inject PedalonsQueryContext pedalonsContext;
  @Inject DomainResolver domainResolver;
  @Inject UserExportBuilder exportBuilder;
  @Inject UserExportEmailService exportEmailService;
  @Inject StorageService storageService;

  @ConfigProperty(name = "pedalons.export.cooldown-hours", defaultValue = "1")
  int cooldownHours;

  @ConfigProperty(name = "pedalons.export.expiry-days", defaultValue = "7")
  int expiryDays;

  @ConfigProperty(name = "pedalons.export.max-bytes", defaultValue = "2147483648")
  long maxBytes;

  @ConfigProperty(name = "pedalons.export.stuck-after-minutes", defaultValue = "60")
  int stuckAfterMinutes;

  @ConfigProperty(name = "pedalons.export.max-attempts", defaultValue = "3")
  int maxAttempts;

  @ConfigProperty(name = "pedalons.export.history-days", defaultValue = "90")
  int historyDays;

  // ------------------------------------------------------------------ requests

  /** Queues an export for the current user, or refuses with 429 if one is running or too recent. */
  @Logged
  @Transactional
  public UserExportDto requestExport() {
    User user = pedalonsContext.getUser();
    Optional<UserExport> latest = userExportRepository.findLatestByUser(user.getId());

    if (latest.isPresent()) {
      UserExport last = latest.get();
      if (last.isInFlight()) {
        throw new TooManyRequestsException(ErrorCode.EXPORT_IN_PROGRESS, 60);
      }
      Instant coolsDownAt = last.getRequestedAt().plus(cooldownHours, ChronoUnit.HOURS);
      if (coolsDownAt.isAfter(Instant.now())) {
        throw new TooManyRequestsException(
            ErrorCode.EXPORT_RATE_LIMITED,
            Duration.between(Instant.now(), coolsDownAt).toSeconds());
      }
    }

    ResolvedSite site = domainResolver.getResolvedSite();
    UserExport export =
        new UserExport(
            site.domain(), user, site.effectiveBaseUrl(), site.effectiveName(), DEFAULT_LANGUAGE);
    try {
      // Flush here rather than at commit: uk_user_exports_active is what catches two concurrent
      // POSTs slipping through the read above, and we want that as a 429, not a 500 from the
      // exception mapper's generic branch.
      userExportRepository.persistAndFlush(export);
    } catch (PersistenceException e) {
      LOG.debugf(e, "Concurrent export request for user %s", TsidUtils.toString(user.getId()));
      throw new TooManyRequestsException(ErrorCode.EXPORT_IN_PROGRESS, 60);
    }
    return UserExportDto.from(export);
  }

  /** The current user's most recent export, if they have ever asked for one. */
  @Logged
  @Transactional
  public Optional<UserExportDto> getLatestExport() {
    return userExportRepository
        .findLatestByUser(pedalonsContext.getUserId())
        .map(UserExportDto::from);
  }

  /** One of the current user's exports. Someone else's job is a 404, never a 403. */
  @Logged
  @Transactional
  public UserExportDto getExport(String exportId) {
    return userExportRepository
        .findByIdAndUser(TsidUtils.toLong(exportId), pedalonsContext.getUserId())
        .map(UserExportDto::from)
        .orElseThrow(NotFoundException::new);
  }

  /**
   * Resolves a download token to the stored archive.
   *
   * <p>Anonymous by design — the token in the emailed link is the credential. Every failure mode
   * (unknown token, expired, wrong domain, deleted user) returns the same 404, so the endpoint says
   * nothing about which one it was.
   */
  @Public
  @Transactional
  public DownloadableExport download(String token) {
    UserExport export =
        userExportRepository
            .findDownloadableByTokenHash(domainResolver.getDomainId(), TokenUtils.hashToken(token))
            .orElseThrow(NotFoundException::new);

    Instant expiresAt = export.getExpiresAt();
    String key = export.getStorageKey();
    // Checked here rather than in the query so an expired row that the nightly sweep has not yet
    // reached still 404s instead of leaking through the gap.
    if (key == null || expiresAt == null || expiresAt.isBefore(Instant.now())) {
      throw new NotFoundException();
    }

    InputStream content = storageService.retrieve(key);
    return new DownloadableExport(content, downloadFileName(export), export.getFileSize());
  }

  private static String downloadFileName(UserExport export) {
    return "pedalons-export-" + FILE_DATE.format(export.getRequestedAt()) + ".zip";
  }

  // ------------------------------------------------------------------ the job

  /**
   * Claims and runs at most one pending export.
   *
   * <p>No {@code @Transactional} on this method on purpose: building the archive takes minutes of
   * storage I/O, and holding a JDBC connection across it would starve the pool. Each step below
   * opens its own short transaction instead.
   */
  public void processOnePendingExport() {
    Optional<Long> claimed = claimNextPending();
    if (claimed.isEmpty()) {
      return;
    }
    long exportId = claimed.get();
    ExportJobContext ctx = QuarkusTransaction.requiringNew().call(() -> loadContext(exportId));

    Path zipFile = null;
    String storageKey = null;
    try {
      zipFile = exportBuilder.build(ctx);
      long size = Files.size(zipFile);
      if (size > maxBytes) {
        throw new IllegalStateException(
            "Export exceeds the %d byte limit (%d bytes)".formatted(maxBytes, size));
      }

      storageKey = KEY_PREFIX + ctx.userTsid() + "/" + ctx.exportTsid() + ".zip";
      try (InputStream in = Files.newInputStream(zipFile)) {
        storageService.store(
            storageKey,
            in,
            "application/zip",
            size,
            Map.of("export-id", ctx.exportTsid(), "user-id", ctx.userTsid()));
      }

      String token = TokenUtils.generateSecureToken();
      Instant expiresAt = Instant.now().plus(expiryDays, ChronoUnit.DAYS);
      markReady(exportId, storageKey, size, TokenUtils.hashToken(token), expiresAt);

      // Deliberately after the commit above: a mail failure must not roll back a good export. The
      // row keeps emailSentAt null and retryPendingEmails picks it up on a later tick.
      exportEmailService.sendExportReady(ctx, token, expiresAt, size);
      markEmailSent(exportId);
      LOG.infof("Export %s ready (%d bytes)", ctx.exportTsid(), size);
    } catch (Exception e) {
      LOG.errorf(e, "Export %s failed", ctx.exportTsid());
      markFailed(exportId, e);
      // The only way an orphaned object can exist is a failure after the upload.
      safeDelete(storageKey);
    } finally {
      deleteQuietly(zipFile);
    }
  }

  /** A READY export still owed its notification email. */
  private record PendingEmail(ExportJobContext ctx, long fileSize) {}

  /** Re-sends the ready-notification for jobs whose email never went out. */
  public void retryPendingEmails() {
    List<PendingEmail> pending =
        QuarkusTransaction.requiringNew()
            .call(
                () ->
                    userExportRepository.findWithUnsentEmail().stream()
                        .map(
                            e ->
                                new PendingEmail(
                                    toContext(e), e.getFileSize() == null ? 0 : e.getFileSize()))
                        .toList());

    for (PendingEmail item : pending) {
      ExportJobContext ctx = item.ctx();
      // Only the hash was kept, so a retry cannot resend the original link — it mints a fresh token
      // and supersedes the old one. The first email, if it ever arrived, stops working.
      String token = TokenUtils.generateSecureToken();
      Instant expiresAt = Instant.now().plus(expiryDays, ChronoUnit.DAYS);
      try {
        boolean rotated =
            QuarkusTransaction.requiringNew()
                .call(
                    () -> {
                      UserExport export = userExportRepository.findById(ctx.exportId());
                      if (export == null || export.getStatus() != UserExportStatus.READY) {
                        return false;
                      }
                      export.setDownloadTokenHash(TokenUtils.hashToken(token));
                      export.setExpiresAt(expiresAt);
                      return true;
                    });
        if (!rotated) {
          continue;
        }
        exportEmailService.sendExportReady(ctx, token, expiresAt, item.fileSize());
        markEmailSent(ctx.exportId());
      } catch (Exception e) {
        LOG.warnf(e, "Retrying export email for %s failed", ctx.exportTsid());
      }
    }
  }

  /**
   * Moves one row from PENDING to PROCESSING, in its own transaction so the row lock is held for
   * microseconds rather than for the whole build.
   */
  Optional<Long> claimNextPending() {
    return QuarkusTransaction.requiringNew()
        .call(
            () -> {
              Long id = userExportRepository.findNextPendingIdSkipLocked();
              if (id == null) {
                return Optional.<Long>empty();
              }
              return userExportRepository.claim(id, Instant.now())
                  ? Optional.of(id)
                  : Optional.<Long>empty();
            });
  }

  private ExportJobContext loadContext(long exportId) {
    UserExport export = userExportRepository.findById(exportId);
    if (export == null) {
      throw new IllegalStateException("Claimed export vanished: " + TsidUtils.toString(exportId));
    }
    return toContext(export);
  }

  private static ExportJobContext toContext(UserExport export) {
    User user = export.getUser();
    return new ExportJobContext(
        export.getId(),
        TsidUtils.toString(export.getId()),
        user.getId(),
        TsidUtils.toString(user.getId()),
        export.getDomain().getId(),
        user.getEmail(),
        user.getDisplayName(),
        export.getBaseUrl(),
        export.getSiteName(),
        export.getLanguage());
  }

  // The three mark* methods below are called from processOnePendingExport, i.e. on `this`, where a
  // @Transactional annotation would be silently bypassed — CDI interceptors only fire through the
  // bean proxy. They open their transactions explicitly instead.

  void markReady(long exportId, String storageKey, long size, String tokenHash, Instant expiresAt) {
    QuarkusTransaction.requiringNew()
        .run(
            () -> {
              UserExport export = userExportRepository.findById(exportId);
              export.setStatus(UserExportStatus.READY);
              export.setStorageKey(storageKey);
              export.setFileSize(size);
              export.setDownloadTokenHash(tokenHash);
              export.setExpiresAt(expiresAt);
              export.setCompletedAt(Instant.now());
              export.setErrorMessage(null);
            });
  }

  void markEmailSent(long exportId) {
    QuarkusTransaction.requiringNew()
        .run(
            () -> {
              UserExport export = userExportRepository.findById(exportId);
              if (export != null) {
                export.setEmailSentAt(Instant.now());
              }
            });
  }

  void markFailed(long exportId, Exception cause) {
    QuarkusTransaction.requiringNew()
        .run(
            () -> {
              UserExport export = userExportRepository.findById(exportId);
              if (export == null) {
                return;
              }
              export.setStorageKey(null);
              export.setErrorMessage(truncate(cause.toString()));
              // Below the attempt ceiling it goes back on the queue for the next tick.
              export.setStatus(
                  export.getAttempts() >= maxAttempts
                      ? UserExportStatus.FAILED
                      : UserExportStatus.PENDING);
            });
  }

  // ------------------------------------------------------------------ cleanup

  /**
   * Expires archives past their retention window.
   *
   * <p>Storage first, then the row — a crash between the two leaves a harmless orphaned row that
   * tomorrow's sweep retries, rather than an orphaned object nothing will ever find.
   */
  @Transactional
  public int purgeExpired() {
    List<UserExport> expired = userExportRepository.findExpired(Instant.now());
    for (UserExport export : expired) {
      safeDelete(export.getStorageKey());
      export.setStorageKey(null);
      // Frees uk_user_exports_token_hash and kills any link still sitting in the user's inbox.
      export.setDownloadTokenHash(null);
      export.setStatus(UserExportStatus.EXPIRED);
    }
    return expired.size();
  }

  /**
   * Recovers jobs claimed but never finished — a crash between claim and completion. Without this
   * they would sit in PROCESSING forever and {@code uk_user_exports_active} would block that user
   * from ever exporting again.
   */
  @Transactional
  public int retryStuckJobs() {
    Instant cutoff = Instant.now().minus(stuckAfterMinutes, ChronoUnit.MINUTES);
    List<UserExport> stuck = userExportRepository.findStuck(cutoff);
    for (UserExport export : stuck) {
      if (export.getAttempts() >= maxAttempts) {
        export.setStatus(UserExportStatus.FAILED);
        export.setErrorMessage("Abandoned after " + export.getAttempts() + " attempts");
      } else {
        export.setStatus(UserExportStatus.PENDING);
        export.setStartedAt(null);
      }
    }
    return stuck.size();
  }

  /** Drops finished rows once they are older than the history window. */
  @Transactional
  public long deleteOldRows() {
    return userExportRepository.deleteOlderThan(Instant.now().minus(historyDays, ChronoUnit.DAYS));
  }

  private void safeDelete(@Nullable String storageKey) {
    if (storageKey == null) {
      return;
    }
    try {
      storageService.delete(storageKey);
    } catch (Exception e) {
      LOG.warnf(e, "Storage cleanup failed for export object %s", storageKey);
    }
  }

  private static void deleteQuietly(@Nullable Path file) {
    if (file == null) {
      return;
    }
    try {
      Files.deleteIfExists(file);
    } catch (IOException e) {
      LOG.warnf(e, "Could not delete export temp file %s", file);
    }
  }

  private static String truncate(String message) {
    return message.length() <= 500 ? message : message.substring(0, 500);
  }
}
