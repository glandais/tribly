package fr.pedalons.service.notification;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.notification.NotificationEventEntry;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.comments.request.CommentRequest;
import fr.pedalons.dto.moderation.request.ModerationDecisionRequest;
import fr.pedalons.dto.moderation.request.ReportRequest;
import fr.pedalons.enums.ModerationAction;
import fr.pedalons.enums.NotificationSubjectType;
import fr.pedalons.enums.NotificationType;
import fr.pedalons.enums.ReportReason;
import fr.pedalons.enums.ReportTargetType;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import fr.pedalons.util.NotificationTestData;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code CONTENT_REPORTED} — who hears of a report — and the comment notifications a block
 * silences. Fixture: team1 has user1 (its only admin), user2 (organizer) and user3 (member); user4
 * and user5 are outsiders. A platform admin is added here.
 */
@QuarkusTest
class ModerationNotificationTest extends AbstractResourceTest {

  @Inject NotificationDispatchService dispatchService;
  @Inject NotificationTestData notifications;

  private final Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);
  private final Instant nextWeek = Instant.now().plus(7, ChronoUnit.DAYS);

  private User platformAdmin;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    platformAdmin = dataService.createPlatformAdminUser("admin@example.com", "Platform Admin");
  }

  private void drain() {
    while (dispatchService.dispatchOne()) {
      // fan out everything queued
    }
  }

  private void report(String user, ReportTargetType type, Long targetId) {
    given()
        .auth()
        .oauth2(getAccessToken(user))
        .contentType("application/json")
        .body(
            new ReportRequest(
                team1Slug, type, TsidUtils.toString(targetId), ReportReason.HARASSMENT, null))
        .when()
        .post("/api/reports")
        .then()
        .statusCode(204);
  }

  private String comment(String user, Ride ride, String content, @Nullable String parentId) {
    return given()
        .auth()
        .oauth2(getAccessToken(user))
        .contentType("application/json")
        .body(new CommentRequest(content, parentId))
        .when()
        .post("/api/teams/" + team1Slug + "/rides/" + ride.getSlug() + "/comments")
        .then()
        .statusCode(201)
        .extract()
        .path("id");
  }

  private void block(String user, User blocked) {
    given()
        .auth()
        .oauth2(getAccessToken(user))
        .when()
        .put("/api/users/me/blocks/" + TsidUtils.toString(blocked.getId()))
        .then()
        .statusCode(204);
  }

  private List<NotificationType> reportsFor(User user) {
    return notifications.notificationTypesFor(user).stream()
        .filter(t -> t == NotificationType.CONTENT_REPORTED)
        .toList();
  }

  private static final List<NotificationType> ONE = List.of(NotificationType.CONTENT_REPORTED);

  // ------------------------------------------------------------------ CONTENT_REPORTED

  @Test
  void report_notifiesTheTeamModeratorsAndPlatformAdmins_neverTheReporterNorTheTarget() {
    dataService.addUserToTeam(user4, team1, TeamRole.MEMBER);

    report("user4", ReportTargetType.MEMBER, user3.getId());
    drain();

    assertEquals(ONE, reportsFor(user1));
    assertEquals(ONE, reportsFor(user2));
    assertEquals(ONE, reportsFor(platformAdmin));
    assertTrue(reportsFor(user3).isEmpty(), "the member reported");
    assertTrue(reportsFor(user4).isEmpty(), "the reporter");

    NotificationEventEntry event = notifications.eventEntries().getFirst();
    assertEquals(NotificationSubjectType.REPORT, event.getSubjectType());
    assertEquals(team1Slug, event.getSubjectSlug());
    assertEquals(team1.getName(), event.getSubjectName());
    assertNull(event.getActorId(), "the reporter stays anonymous");
    assertNull(event.getActorName());
    assertNull(event.getExcerpt(), "a push shows on the lock screen");
    assertTrue(
        event.getDedupKey().startsWith("CONTENT_REPORTED:" + team1.getId() + ":MEMBER:"),
        event.getDedupKey());
    assertEquals(
        "/teams/" + team1Slug + "/admin/reports",
        NotificationLinks.subjectPath(NotificationSubjectType.REPORT, team1Slug, team1Slug));
  }

  @Test
  void reportAboutAnOrganizer_reachesTheOtherAdminsOnly() {
    Post post = dataService.createPost(team1, user2, "Billet", yesterday, Visibility.PUBLIC);

    report(USER3, ReportTargetType.POST, post.getId());
    drain();

    assertEquals(ONE, reportsFor(user1));
    assertEquals(ONE, reportsFor(platformAdmin));
    assertTrue(reportsFor(user2).isEmpty(), "the organizer reported");
  }

  @Test
  void reportAboutTheOnlyAdmin_reachesThePlatformAdminsOnly() {
    Post post = dataService.createPost(team1, user1, "Billet", yesterday, Visibility.PUBLIC);

    report(USER3, ReportTargetType.POST, post.getId());
    drain();

    assertEquals(ONE, reportsFor(platformAdmin));
    assertTrue(reportsFor(user1).isEmpty(), "the admin reported");
    assertTrue(reportsFor(user2).isEmpty(), "an organizer does not judge an admin");
  }

  @Test
  void aBurstOfReports_onOneTarget_makesOneNotification() {
    Post post = dataService.createPost(team1, user2, "Billet", yesterday, Visibility.PUBLIC);

    report(USER3, ReportTargetType.POST, post.getId());
    report(USER4, ReportTargetType.POST, post.getId());
    report(USER5, ReportTargetType.POST, post.getId());
    drain();

    assertEquals(1, notifications.events().size());
    assertEquals(ONE, reportsFor(user1));
  }

  @Test
  void aReportDecidedBeforeTheFanOut_notifiesNobody() {
    Post post = dataService.createPost(team1, user2, "Billet", yesterday, Visibility.PUBLIC);
    report(USER3, ReportTargetType.POST, post.getId());
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(
            new ModerationDecisionRequest(
                ReportTargetType.POST, TsidUtils.toString(post.getId()), ModerationAction.DISMISS))
        .when()
        .post("/api/teams/" + team1Slug + "/reports/resolve")
        .then()
        .statusCode(204);

    drain();

    assertTrue(reportsFor(user1).isEmpty());
    assertTrue(reportsFor(platformAdmin).isEmpty());
  }

  // ------------------------------------------------------------------ blocks

  @Test
  void aReply_toSomeoneWhoBlockedTheReplier_isNotSent() {
    Ride ride = dataService.createRide(team1, user1, "Sortie", "sortie", nextWeek);
    block(USER3, user2);
    String rootId = comment(USER3, ride, "On part à quelle heure ?", null);
    comment(USER2, ride, "8h30", rootId);
    drain();

    assertTrue(notifications.notificationTypesFor(user3).isEmpty());
    // The ride's author still hears of the new thread.
    assertEquals(
        List.of(NotificationType.COMMENT_ON_MY_PUBLICATION),
        notifications.notificationTypesFor(user1));
  }

  @Test
  void aComment_onThePublicationOfSomeoneWhoBlockedTheCommenter_isNotSent() {
    Ride ride = dataService.createRide(team1, user1, "Sortie", "sortie", nextWeek);
    block(USER1, user3);
    comment(USER3, ride, "Je viens !", null);
    drain();

    assertTrue(notifications.notificationTypesFor(user1).isEmpty());
  }
}
