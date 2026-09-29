package fr.pedalons.api.feedback;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.feedback.ErrorSignature;
import fr.pedalons.domain.feedback.FeedbackReport;
import fr.pedalons.dto.feedback.request.ClientContextDto;
import fr.pedalons.dto.feedback.request.ClientErrorDto;
import fr.pedalons.dto.feedback.request.ClientLogEntryDto;
import fr.pedalons.dto.feedback.request.ErrorReportRequest;
import fr.pedalons.dto.feedback.request.FeedbackRequest;
import fr.pedalons.enums.ClientLogLevel;
import fr.pedalons.enums.ClientPlatform;
import fr.pedalons.enums.FeedbackKind;
import fr.pedalons.enums.GithubSyncStatus;
import fr.pedalons.infrastructure.github.GitHubException;
import fr.pedalons.infrastructure.github.GitHubIssueClient;
import fr.pedalons.infrastructure.github.GitHubIssueClient.IssueStatus;
import fr.pedalons.repository.feedback.ErrorOccurrenceRepository;
import fr.pedalons.repository.feedback.ErrorSignatureRepository;
import fr.pedalons.repository.feedback.FeedbackReportRepository;
import fr.pedalons.service.feedback.FeedbackGithubWorker;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * In-app feedback and automatic error reports, from the request to the GitHub issue.
 *
 * <p>{@link GitHubIssueClient} is mocked, as {@code FcmClient} is in {@code PushNotificationTest}:
 * this is about what gets stored, redacted, deduplicated and published, not about GitHub's wire
 * format. The scheduler is off in tests; the worker's passes are called by hand.
 */
@QuarkusTest
class FeedbackResourceTest extends AbstractResourceTest {

  private static final String JWT =
      "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ1c2VyMSJ9.c2lnbmF0dXJlLW9mLXRoZS10b2tlbg";

  private static final String WEB_STACK =
      """
      TypeError: Cannot read properties of undefined (reading 'name')
          at Xe (https://pedalons.fr/assets/RideDetailPage-Bx1_aZ9q.js:1:2345)
          at Yf (https://pedalons.fr/assets/index-C9aa0Zz1.js:4:111)
          at chrome-extension://abcdef/content.js:1:1
      """;

  @InjectMock GitHubIssueClient github;

  @Inject FeedbackGithubWorker worker;
  @Inject FeedbackReportRepository feedbackReportRepository;
  @Inject ErrorSignatureRepository errorSignatureRepository;
  @Inject ErrorOccurrenceRepository errorOccurrenceRepository;

  private int nextIssue;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    nextIssue = 100;
    when(github.isConfigured()).thenReturn(true);
    when(github.createIssue(anyString(), anyString(), anyList())).thenAnswer(i -> nextIssue++);
    when(github.status(anyInt())).thenReturn(new IssueStatus(false, null));
  }

  // ------------------------------------------------------------------ fixtures

  private static ClientContextDto web(String version) {
    return new ClientContextDto(
        ClientPlatform.WEB,
        version,
        null,
        null,
        null,
        "Mozilla/5.0",
        "/teams/team1/rides/abc?token=secret-invite-token",
        "fr",
        "Europe/Paris",
        "team1");
  }

  private static List<ClientLogEntryDto> logs() {
    return List.of(
        new ClientLogEntryDto(Instant.now(), ClientLogLevel.INFO, "navigation", "/teams/team1"),
        new ClientLogEntryDto(
            Instant.now(),
            ClientLogLevel.ERROR,
            "http",
            "GET /api/teams/team1 500 Authorization: Bearer " + JWT + " for someone@example.com"));
  }

  private static FeedbackRequest feedback(@Nullable ClientErrorDto error) {
    return new FeedbackRequest(
        FeedbackKind.BUG, "La page de la sortie reste blanche", web("abc123"), error, logs());
  }

  private static ErrorReportRequest errorReport(String version, String message, String stack) {
    return new ErrorReportRequest(
        web(version), new ClientErrorDto("TypeError", message, stack), logs());
  }

  private void sendFeedback(String user, FeedbackRequest request, int status) {
    given()
        .auth()
        .oauth2(getAccessToken(user))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/feedback")
        .then()
        .statusCode(status);
  }

  private void sendError(String user, ErrorReportRequest request) {
    given()
        .auth()
        .oauth2(getAccessToken(user))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/feedback/errors")
        .then()
        .statusCode(204);
  }

  private List<FeedbackReport> reports() {
    return QuarkusTransaction.requiringNew().call(() -> feedbackReportRepository.listAll());
  }

  private List<ErrorSignature> signatures() {
    return QuarkusTransaction.requiringNew().call(() -> errorSignatureRepository.listAll());
  }

  private long occurrences() {
    return QuarkusTransaction.requiringNew().call(() -> errorOccurrenceRepository.count());
  }

  // ------------------------------------------------------------------ written reports

  @Test
  void feedback_requiresAMember() {
    given()
        .contentType("application/json")
        .body(feedback(null))
        .when()
        .post("/api/feedback")
        .then()
        .statusCode(401);
  }

  @Test
  void feedback_isStoredRedactedAndPending() {
    sendFeedback(USER1, feedback(null), 204);

    List<FeedbackReport> reports = reports();
    assertEquals(1, reports.size());
    FeedbackReport report = reports.getFirst();
    assertEquals(GithubSyncStatus.PENDING, report.getGithubStatus());
    assertEquals("La page de la sortie reste blanche", report.getMessage());
    assertEquals("/teams/team1/rides/abc", report.getContext().get("route").asText());
    String storedLogs = report.getLogs().toString();
    assertFalse(storedLogs.contains(JWT), storedLogs);
    assertFalse(storedLogs.contains("someone@example.com"), storedLogs);
    assertTrue(storedLogs.contains("[redacted]"), storedLogs);
  }

  @Test
  void feedback_tooShort_isRejected() {
    sendFeedback(
        USER1, new FeedbackRequest(FeedbackKind.BUG, "bug", web("abc123"), null, null), 400);
  }

  @Test
  void feedback_bugWithoutMessage_isPublishedUnderItsError() {
    ClientErrorDto error = new ClientErrorDto("TypeError", "x is undefined", WEB_STACK);
    sendFeedback(
        USER1, new FeedbackRequest(FeedbackKind.BUG, null, web("abc123"), error, logs()), 204);

    assertNull(reports().getFirst().getMessage());
    worker.publishPending();
    ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
    verify(github)
        .createIssue(eq("[Bug][web] TypeError: x is undefined"), body.capture(), anyList());
    assertTrue(body.getValue().contains("Pas de description du membre"), body.getValue());
  }

  @Test
  void feedback_suggestionWithoutMessage_isRejected() {
    sendFeedback(
        USER1, new FeedbackRequest(FeedbackKind.SUGGESTION, null, web("abc123"), null, null), 400);
    assertTrue(reports().isEmpty());
  }

  @Test
  void feedback_isRateLimitedPerMember() {
    for (int i = 0; i < 5; i++) {
      sendFeedback(USER1, feedback(null), 204);
    }
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(feedback(null))
        .when()
        .post("/api/feedback")
        .then()
        .statusCode(429)
        .body("code", equalTo("FEEDBACK_RATE_LIMITED"));
    // Another member is not affected.
    sendFeedback(USER2, feedback(null), 204);
  }

  @Test
  void feedback_isPublishedAsAnIssue_withoutTheMembersAddress() {
    sendFeedback(USER1, feedback(null), 204);

    worker.publishPending();

    ArgumentCaptor<String> title = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<String>> labels = ArgumentCaptor.forClass(List.class);
    verify(github).createIssue(title.capture(), body.capture(), labels.capture());
    assertTrue(title.getValue().startsWith("[Bug][web] La page de la sortie"), title.getValue());
    assertTrue(labels.getValue().containsAll(List.of("feedback", "bug", "web", "localhost")));
    assertFalse(body.getValue().contains(EMAIL1), body.getValue());
    assertFalse(body.getValue().contains(JWT), body.getValue());
    assertFalse(body.getValue().contains("secret-invite-token"), body.getValue());

    FeedbackReport report = reports().getFirst();
    assertEquals(GithubSyncStatus.CREATED, report.getGithubStatus());
    assertEquals(100, report.getGithubIssueNumber());

    // Published once only.
    worker.publishPending();
    verify(github, times(1)).createIssue(anyString(), anyString(), anyList());
  }

  @Test
  void feedback_isRetried_thenGivenUp() {
    when(github.createIssue(anyString(), anyString(), anyList()))
        .thenThrow(new GitHubException("GitHub answered 502"));
    sendFeedback(USER1, feedback(null), 204);

    worker.publishPending();
    FeedbackReport report = reports().getFirst();
    assertEquals(GithubSyncStatus.PENDING, report.getGithubStatus());
    assertEquals(1, report.getGithubAttempts());

    for (int i = 1; i < 5; i++) {
      worker.publishPending();
    }
    assertEquals(GithubSyncStatus.FAILED, reports().getFirst().getGithubStatus());
  }

  @Test
  void feedback_withoutGithub_waitsInTheDatabase() {
    when(github.isConfigured()).thenReturn(false);
    sendFeedback(USER1, feedback(null), 204);

    worker.publishPending();

    verify(github, never()).createIssue(anyString(), anyString(), anyList());
    assertEquals(GithubSyncStatus.PENDING, reports().getFirst().getGithubStatus());
  }

  @Test
  void feedback_fromAKnownError_linksBothIssues() {
    sendError(USER2, errorReport("abc123", "Cannot read properties of undefined", WEB_STACK));
    worker.publishPending(); // crash issue #100

    sendFeedback(
        USER1,
        feedback(new ClientErrorDto("TypeError", "Cannot read properties of undefined", WEB_STACK)),
        204);
    worker.publishPending(); // feedback issue #101

    ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
    verify(github).createIssue(contains("[Bug]"), body.capture(), anyList());
    assertTrue(body.getValue().contains("#100"), body.getValue());
    verify(github).comment(eq(100), contains("#101"));
  }

  // ------------------------------------------------------------------ automatic error reports

  @Test
  void errors_sameBugOnTwoRides_isOneSignatureAndOneIssue() {
    sendError(USER1, errorReport("abc123", "Ride 0j6fq2zyd3kxb not found (404)", WEB_STACK));
    sendError(
        USER2,
        errorReport(
            "abc123",
            "Ride 0k1abcd2efgh3 not found (404)",
            WEB_STACK.replace(":1:2345", ":1:9999").replace("Bx1_aZ9q", "Dd2_bY8r")));

    List<ErrorSignature> signatures = signatures();
    assertEquals(1, signatures.size());
    assertEquals(2, signatures.getFirst().getOccurrenceCount());

    worker.publishPending();
    worker.publishPending();
    verify(github, times(1))
        .createIssue(
            contains("[crash][web]"), anyString(), eq(List.of("crash", "web", "localhost")));
    assertEquals(GithubSyncStatus.CREATED, signatures().getFirst().getGithubStatus());
  }

  @Test
  void errors_differentBugs_areDifferentSignatures() {
    sendError(USER1, errorReport("abc123", "Cannot read properties of undefined", WEB_STACK));
    sendError(
        USER1,
        errorReport(
            "abc123",
            "Cannot read properties of undefined",
            WEB_STACK.replace("RideDetailPage", "TripDetailPage")));
    assertEquals(2, signatures().size());
  }

  @Test
  void errors_pastTheQuota_areDroppedSilently() {
    for (int i = 0; i < 21; i++) {
      sendError(USER1, errorReport("abc123", "Boom", WEB_STACK));
    }
    assertEquals(20, occurrences());
  }

  @Test
  void errors_newIssuesAreCappedPerDay() {
    for (int i = 0; i < 11; i++) {
      sendError(
          USER1,
          new ErrorReportRequest(
              web("abc123"), new ClientErrorDto("Error" + (char) ('A' + i), "Boom", null), null));
    }
    worker.publishPending();

    verify(github, times(10)).createIssue(anyString(), anyString(), anyList());
    long pending =
        signatures().stream().filter(s -> s.getGithubStatus() == GithubSyncStatus.PENDING).count();
    assertEquals(1, pending);
  }

  @Test
  void errors_areSummarizedOncePerNewBatch() {
    sendError(USER1, errorReport("abc123", "Boom", WEB_STACK));
    worker.publishPending(); // #100

    sendError(USER2, errorReport("abc124", "Boom", WEB_STACK));
    sendError(USER3, errorReport("abc124", "Boom", WEB_STACK));
    worker.summarize();
    verify(github).comment(eq(100), contains("+2 occurrences"));

    worker.summarize();
    verify(github, times(1)).comment(eq(100), anyString());
  }

  @Test
  void errors_closedIssue_isReopenedByANewVersionOnly() {
    sendError(USER1, errorReport("abc123", "Boom", WEB_STACK));
    worker.publishPending(); // #100

    // Closed on GitHub; an old client still hits it, which the daily pass notices.
    sendError(USER2, errorReport("abc123", "Boom", WEB_STACK));
    when(github.status(100)).thenReturn(new IssueStatus(true, Instant.now()));
    worker.summarize();
    ErrorSignature closed = signatures().getFirst();
    assertTrue(closed.isIssueClosed());
    assertEquals(List.of("abc123"), closed.getVersionsAtClose());
    assertNull(closed.getRegressionVersion());

    sendError(USER2, errorReport("abc123", "Boom", WEB_STACK));
    worker.publishPending();
    verify(github, never()).reopen(anyInt());

    sendError(USER2, errorReport("def456", "Boom", WEB_STACK));
    assertEquals("def456", signatures().getFirst().getRegressionVersion());
    worker.publishPending();
    verify(github).reopen(100);
    verify(github).comment(eq(100), contains("def456"));
    ErrorSignature reopened = signatures().getFirst();
    assertFalse(reopened.isIssueClosed());
    assertNull(reopened.getRegressionVersion());
    assertNotNull(reopened.getSummarizedUntil());
  }

  @Test
  void errors_redactTheStack() {
    sendError(
        USER1,
        errorReport(
            "abc123", "Token " + JWT + " refused", WEB_STACK + "\n at x (mailto:" + EMAIL1 + ")"));
    String stored =
        QuarkusTransaction.requiringNew()
            .call(() -> errorOccurrenceRepository.findAll().firstResult().getError().toString());
    assertFalse(stored.contains(JWT), stored);
    assertFalse(stored.contains(EMAIL1), stored);
  }
}
