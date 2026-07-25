package fr.pedalons.service.user;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.exception.TooManyRequestsException;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.domain.user.UserExport;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.enums.AssetType;
import fr.pedalons.enums.UserExportStatus;
import fr.pedalons.enums.Visibility;
import fr.pedalons.infrastructure.storage.StorageService;
import fr.pedalons.repository.user.UserExportRepository;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserExportServiceTest extends AbstractBaseTest {

  @Inject UserExportService userExportService;
  @Inject UserExportRepository userExportRepository;
  @Inject StorageService storageService;
  @Inject PedalonsQueryContext context;
  @Inject DomainResolver domainResolver;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject MockMailbox mailbox;

  private Domain domain;
  private User user;
  private User otherUser;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    mailbox.clear();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    user = dataService.createUser(domain, "owner@example.com", "Owner");
    otherUser = dataService.createUser(domain, "other@example.com", "Other");
    context.setUserForTest(user);
  }

  // -------------------------------------------------------------------- claim

  @Test
  void claimNextPending_shouldMoveTheRowToProcessing() {
    UserExport export = dataService.createUserExport(domain, user);

    Optional<Long> claimed = userExportService.claimNextPending();

    assertTrue(claimed.isPresent());
    assertEquals(export.getId(), claimed.get());
    UserExport reloaded = dataService.reloadUserExport(export.getId());
    assertEquals(UserExportStatus.PROCESSING, reloaded.getStatus());
    assertNotNull(reloaded.getStartedAt());
    assertEquals(1, reloaded.getAttempts());
  }

  /** The compare-and-set is what stops two workers running the same job. */
  @Test
  void claimNextPending_shouldNotHandOutTheSameRowTwice() {
    dataService.createUserExport(domain, user);

    assertTrue(userExportService.claimNextPending().isPresent());
    assertTrue(userExportService.claimNextPending().isEmpty());
  }

  @Test
  void claimNextPending_withNothingQueued_shouldReturnEmpty() {
    assertTrue(userExportService.claimNextPending().isEmpty());
  }

  // ------------------------------------------------------------- full run

  @Test
  void processOnePendingExport_shouldStoreAnArchiveAndEmailALink() {
    seedContent(user, "Owner Team", "owner-team");
    UserExport queued = dataService.createUserExport(domain, user);

    userExportService.processOnePendingExport();

    UserExport done = dataService.reloadUserExport(queued.getId());
    assertEquals(UserExportStatus.READY, done.getStatus());
    assertNotNull(done.getStorageKey());
    assertTrue(storageService.exists(done.getStorageKey()));
    assertNotNull(done.getFileSize());
    assertTrue(done.getFileSize() > 0);
    assertNotNull(done.getCompletedAt());
    assertNotNull(done.getExpiresAt());
    assertNotNull(done.getEmailSentAt());

    var messages = mailbox.getMailMessagesSentTo("owner@example.com");
    assertEquals(1, messages.size());
    String body = messages.getFirst().getText();
    assertNotNull(body);
    assertTrue(
        body.contains(domain.getBaseUrl() + "/api/export/download/"),
        "email should carry a download link built from the snapshotted base URL, was: " + body);
  }

  /** Only the hash is kept, so the plaintext token cannot be recovered from the row. */
  @Test
  void processOnePendingExport_shouldStoreOnlyTheTokenHash() {
    UserExport queued = dataService.createUserExport(domain, user);

    userExportService.processOnePendingExport();

    UserExport done = dataService.reloadUserExport(queued.getId());
    String hash = done.getDownloadTokenHash();
    assertNotNull(hash);

    String emailedToken = extractToken(mailbox.getMailMessagesSentTo("owner@example.com"));
    assertNotEquals(emailedToken, hash);
    assertFalse(hash.contains(emailedToken));
  }

  /**
   * A user's export must contain nothing belonging to another domain, even when the other domain
   * holds content created by a same-named user.
   */
  @Test
  void processOnePendingExport_shouldNotReachIntoAnotherDomain() {
    Domain other = dataService.createDomain("other.localhost", "Other", "http://other.localhost");
    User otherDomainUser = dataService.createUser(other, "owner@example.com", "Owner Elsewhere");
    seedContent(otherDomainUser, "Foreign Team", "foreign-team");

    seedContent(user, "Owner Team", "owner-team");
    UserExport queued = dataService.createUserExport(domain, user);

    userExportService.processOnePendingExport();

    UserExport done = dataService.reloadUserExport(queued.getId());
    assertEquals(UserExportStatus.READY, done.getStatus());

    String teams = ZipReader.entryText(storageService, done.getStorageKey(), "content/teams.json");
    assertTrue(teams.contains("owner-team"));
    assertFalse(teams.contains("foreign-team"), "export leaked another domain's content");
  }

  // ------------------------------------------------------------------ limits

  @Test
  void requestExport_whileOneIsInFlight_shouldThrow() {
    dataService.createUserExport(domain, user);

    TooManyRequestsException e =
        assertThrows(TooManyRequestsException.class, () -> userExportService.requestExport());
    assertEquals(ErrorCode.EXPORT_IN_PROGRESS, e.getErrorCode());
  }

  @Test
  void requestExport_withinTheCooldown_shouldThrowWithARetryAfter() {
    UserExport previous = dataService.createUserExport(domain, user, UserExportStatus.READY);
    dataService.backdateUserExport(previous, 30);

    TooManyRequestsException e =
        assertThrows(TooManyRequestsException.class, () -> userExportService.requestExport());
    assertEquals(ErrorCode.EXPORT_RATE_LIMITED, e.getErrorCode());
    assertTrue(e.getRetryAfterSeconds() > 0);
  }

  // ----------------------------------------------------------------- recovery

  @Test
  void retryStuckJobs_shouldRequeueACrashedJob() {
    UserExport export = dataService.createUserExport(domain, user, UserExportStatus.PROCESSING);
    export.setStartedAt(Instant.now().minus(2, ChronoUnit.HOURS));
    export.setAttempts(1);
    dataService.updateUserExport(export);

    assertEquals(1, userExportService.retryStuckJobs());
    assertEquals(
        UserExportStatus.PENDING, dataService.reloadUserExport(export.getId()).getStatus());
  }

  @Test
  void retryStuckJobs_pastTheAttemptCeiling_shouldGiveUp() {
    UserExport export = dataService.createUserExport(domain, user, UserExportStatus.PROCESSING);
    export.setStartedAt(Instant.now().minus(2, ChronoUnit.HOURS));
    export.setAttempts(3);
    dataService.updateUserExport(export);

    assertEquals(1, userExportService.retryStuckJobs());
    assertEquals(UserExportStatus.FAILED, dataService.reloadUserExport(export.getId()).getStatus());
  }

  @Test
  void purgeExpired_shouldDeleteTheObjectAndClearTheToken() {
    UserExport queued = dataService.createUserExport(domain, user);
    userExportService.processOnePendingExport();
    UserExport ready = dataService.reloadUserExport(queued.getId());
    String key = ready.getStorageKey();
    assertNotNull(key);
    ready.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));
    dataService.updateUserExport(ready);

    assertEquals(1, userExportService.purgeExpired());

    UserExport purged = dataService.reloadUserExport(queued.getId());
    assertEquals(UserExportStatus.EXPIRED, purged.getStatus());
    assertNull(purged.getStorageKey());
    assertNull(purged.getDownloadTokenHash());
    assertFalse(storageService.exists(key));
  }

  @Test
  void purgeExpired_shouldLeaveLiveExportsAlone() {
    UserExport queued = dataService.createUserExport(domain, user);
    userExportService.processOnePendingExport();

    assertEquals(0, userExportService.purgeExpired());
    assertEquals(UserExportStatus.READY, dataService.reloadUserExport(queued.getId()).getStatus());
  }

  // ------------------------------------------------------------------ helpers

  private void seedContent(User owner, String teamName, String teamSlug) {
    Team team =
        dataService.createTeam(owner.getDomain(), owner, teamName, teamSlug, Visibility.PUBLIC);
    dataService.createAsset(team, owner, AssetType.IMAGE, "photo.jpg");
  }

  private static String extractToken(List<io.vertx.ext.mail.MailMessage> messages) {
    String body = messages.getFirst().getText();
    assertNotNull(body);
    int start = body.indexOf("/api/export/download/") + "/api/export/download/".length();
    int end = start;
    while (end < body.length() && !Character.isWhitespace(body.charAt(end))) {
      end++;
    }
    return body.substring(start, end);
  }
}
