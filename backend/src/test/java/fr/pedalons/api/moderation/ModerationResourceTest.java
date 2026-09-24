package fr.pedalons.api.moderation;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.moderation.request.ModerationDecisionRequest;
import fr.pedalons.dto.moderation.request.ReportRequest;
import fr.pedalons.enums.ModerationAction;
import fr.pedalons.enums.ReportReason;
import fr.pedalons.enums.ReportStatus;
import fr.pedalons.enums.ReportTargetType;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The team and platform moderation queues, and their decisions. Fixture: team1 is public, with
 * user1 (admin), user2 (organizer) and user3 (member); user4 and user5 belong to no team. A
 * platform admin and a second member are added here.
 */
@QuarkusTest
class ModerationResourceTest extends AbstractResourceTest {

  private static final String ADMIN = "admin";
  private static final String USER6 = "user6";

  private final Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);

  private User platformAdmin;
  private User user6;
  private Post post;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    platformAdmin = dataService.createPlatformAdminUser("admin@example.com", "Platform Admin");
    user6 = dataService.createUser("user6@example.com", "Test User 6");
    dataService.addUserToTeam(user6, team1, TeamRole.MEMBER);
    post = dataService.createPost(team1, user1, "Compte rendu", yesterday, Visibility.PUBLIC);
  }

  private String teamReports() {
    return "/api/teams/" + team1Slug + "/reports";
  }

  private void report(
      String user, ReportTargetType type, Long targetId, ReportReason reason, @Nullable String m) {
    given()
        .auth()
        .oauth2(getAccessToken(user))
        .contentType("application/json")
        .body(new ReportRequest(team1Slug, type, TsidUtils.toString(targetId), reason, m))
        .when()
        .post("/api/reports")
        .then()
        .statusCode(204);
  }

  private void report(String user, ReportTargetType type, Long targetId) {
    report(user, type, targetId, ReportReason.SPAM, null);
  }

  private ValidatableResponse get(String user, String path) {
    return given().auth().oauth2(getAccessToken(user)).when().get(path).then();
  }

  private ValidatableResponse resolve(
      String user, String path, ReportTargetType type, Long targetId, ModerationAction action) {
    return given()
        .auth()
        .oauth2(getAccessToken(user))
        .contentType("application/json")
        .body(new ModerationDecisionRequest(type, TsidUtils.toString(targetId), action))
        .when()
        .post(path)
        .then();
  }

  // ------------------------------------------------------------------ access

  @Test
  void teamQueue_isForOrganizersAdminsAndPlatformAdmins() {
    get(USER3, teamReports()).statusCode(403);
    get(USER4, teamReports()).statusCode(403);
    get(USER2, teamReports()).statusCode(200);
    get(USER1, teamReports()).statusCode(200);
    get(ADMIN, teamReports()).statusCode(200);
    given().when().get(teamReports()).then().statusCode(401);
  }

  @Test
  void platformQueue_isForPlatformAdminsOnly() {
    get(USER1, "/api/admin/reports").statusCode(403);
    get(ADMIN, "/api/admin/reports").statusCode(200);
  }

  // ------------------------------------------------------------------ grouping and reporters

  @Test
  void teamQueue_groupsReportsByTarget_withoutReporters() {
    report(USER3, ReportTargetType.POST, post.getId(), ReportReason.SPAM, "Pub déguisée");
    report(USER4, ReportTargetType.POST, post.getId(), ReportReason.HARASSMENT, "  ");
    report(USER5, ReportTargetType.POST, post.getId(), ReportReason.SPAM, null);

    get(USER2, teamReports())
        .statusCode(200)
        .body("total", equalTo(1))
        .body("items", hasSize(1))
        .body("items[0].targetType", equalTo("POST"))
        .body("items[0].targetId", equalTo(TsidUtils.toString(post.getId())))
        .body("items[0].teamSlug", equalTo(team1Slug))
        .body("items[0].targetUser.id", equalTo(TsidUtils.toString(user1.getId())))
        .body("items[0].contentName", equalTo("Compte rendu"))
        .body("items[0].contentType", equalTo("POST"))
        .body("items[0].contentSlug", equalTo(post.getSlug()))
        .body("items[0].reportCount", equalTo(3))
        .body("items[0].reasons", containsInAnyOrder("SPAM", "HARASSMENT"))
        .body("items[0].messages", equalTo(List.of("Pub déguisée")))
        .body("items[0].status", equalTo("OPEN"))
        .body("items[0].reporters", nullValue());
  }

  @Test
  void platformQueue_showsTheReporters() {
    report(USER3, ReportTargetType.POST, post.getId());
    report(USER4, ReportTargetType.POST, post.getId());

    get(ADMIN, "/api/admin/reports")
        .statusCode(200)
        .body("items", hasSize(1))
        .body("items[0].reporters.displayName", containsInAnyOrder("Test User 3", "Test User 4"));
    // Not through the team queue, even for a platform admin.
    get(ADMIN, teamReports()).statusCode(200).body("items[0].reporters", nullValue());
  }

  @Test
  void teamQueue_leavesOutTheReportsAboutTheCaller() {
    Post byOrganizer = dataService.createPost(team1, user2, "Billet de l'organisateur", yesterday);
    report(USER3, ReportTargetType.POST, byOrganizer.getId());
    report(USER3, ReportTargetType.MEMBER, user2.getId());

    get(USER2, teamReports()).statusCode(200).body("items", hasSize(0));
    get(USER1, teamReports()).statusCode(200).body("items", hasSize(2));
    // Nor can the organizer decide about themselves.
    resolve(
            USER2,
            teamReports() + "/resolve",
            ReportTargetType.POST,
            byOrganizer.getId(),
            ModerationAction.DISMISS)
        .statusCode(404);
  }

  // ------------------------------------------------------------------ auto-hide

  @Test
  void thirdDistinctReporter_hidesTheContentFromMembers_butNotFromModerators() {
    report(USER3, ReportTargetType.POST, post.getId());
    report(USER4, ReportTargetType.POST, post.getId());
    assertArrayEquals(new boolean[] {false, false}, dataService.teamEntityState(post));

    report(USER5, ReportTargetType.POST, post.getId());
    assertArrayEquals(new boolean[] {false, true}, dataService.teamEntityState(post));

    String detail = "/api/teams/" + team1Slug + "/posts/" + post.getSlug();
    given().when().get(detail).then().statusCode(404);
    get(USER6, detail).statusCode(404);
    get(USER2, detail).statusCode(200);
    get(ADMIN, detail).statusCode(200);
    get(USER6, "/api/teams/" + team1Slug + "/publications?type=POST")
        .statusCode(200)
        .body("publications.slug", not(hasItem(post.getSlug())));
    get(USER2, "/api/teams/" + team1Slug + "/publications?type=POST")
        .statusCode(200)
        .body("publications.slug", hasItem(post.getSlug()));
    get(USER2, teamReports()).statusCode(200).body("items[0].hidden", equalTo(true));
  }

  @Test
  void thirdDistinctReporter_hidesACommentFromMembers() {
    Comment comment = dataService.createComment(user2, post, "Hors sujet");
    dataService.addUserToTeam(user4, team1, TeamRole.MEMBER);
    dataService.addUserToTeam(user5, team1, TeamRole.MEMBER);
    report(USER3, ReportTargetType.COMMENT, comment.getId());
    report(USER4, ReportTargetType.COMMENT, comment.getId());
    report(USER5, ReportTargetType.COMMENT, comment.getId());

    assertNotNull(dataService.findComment(comment.getId()).getModerationHiddenAt());
    String comments = "/api/teams/" + team1Slug + "/posts/" + post.getSlug() + "/comments";
    get(USER6, comments).statusCode(200).body("items", hasSize(0));
    get(USER1, comments).statusCode(200).body("items", hasSize(1));
  }

  // ------------------------------------------------------------------ decisions

  @Test
  void removeContent_deletesThePublication_andClosesEveryOpenReport() {
    report(USER3, ReportTargetType.POST, post.getId());
    report(USER4, ReportTargetType.POST, post.getId());

    resolve(
            USER2,
            teamReports() + "/resolve",
            ReportTargetType.POST,
            post.getId(),
            ModerationAction.REMOVE_CONTENT)
        .statusCode(204);

    assertArrayEquals(new boolean[] {true, false}, dataService.teamEntityState(post));
    assertEquals(
        List.of(ReportStatus.REMOVED, ReportStatus.REMOVED),
        dataService.reportStatuses(ReportTargetType.POST, post.getId()));
    get(USER2, teamReports()).statusCode(200).body("items", hasSize(0));
    get(USER2, teamReports() + "?status=RESOLVED")
        .statusCode(200)
        .body("items", hasSize(1))
        .body("items[0].status", equalTo("REMOVED"))
        .body("items[0].reportCount", equalTo(2));
  }

  /**
   * The post is user1's, the team's admin: the undelete of the post service must not let them put
   * it back past the decision. A platform admin still can.
   */
  @Test
  void removeContent_cannotBeUndoneByTheTeam_onlyByAPlatformAdmin() {
    report(USER3, ReportTargetType.POST, post.getId());
    resolve(
            USER2,
            teamReports() + "/resolve",
            ReportTargetType.POST,
            post.getId(),
            ModerationAction.REMOVE_CONTENT)
        .statusCode(204);
    String undelete = "/api/teams/" + team1Slug + "/posts/" + post.getSlug() + "/undelete";

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType(ContentType.JSON)
        .when()
        .post(undelete)
        .then()
        .statusCode(403);
    assertArrayEquals(new boolean[] {true, false}, dataService.teamEntityState(post));

    given()
        .auth()
        .oauth2(getAccessToken(ADMIN))
        .contentType(ContentType.JSON)
        .when()
        .post(undelete)
        .then()
        .statusCode(200);
    assertArrayEquals(new boolean[] {false, false}, dataService.teamEntityState(post));
  }

  @Test
  void removeContent_ofAComment_deletesItWithItsReplies() {
    Comment root = dataService.createComment(user3, post, "Racine");
    Comment reply = dataService.createReply(user6, post, root, "Réponse");
    report(USER6, ReportTargetType.COMMENT, root.getId());
    report(USER3, ReportTargetType.COMMENT, reply.getId());

    resolve(
            ADMIN,
            "/api/admin/reports/resolve",
            ReportTargetType.COMMENT,
            root.getId(),
            ModerationAction.REMOVE_CONTENT)
        .statusCode(204);

    assertNull(dataService.findComment(root.getId()));
    assertNull(dataService.findComment(reply.getId()));
    assertEquals(
        List.of(ReportStatus.REMOVED),
        dataService.reportStatuses(ReportTargetType.COMMENT, root.getId()));
    // The reply went with its thread; its report is decided too.
    assertEquals(
        List.of(ReportStatus.REMOVED),
        dataService.reportStatuses(ReportTargetType.COMMENT, reply.getId()));
    // The report outlives the comment, excerpt included.
    get(ADMIN, "/api/admin/reports?status=RESOLVED")
        .statusCode(200)
        .body("items.excerpt", hasItem("Racine"));
  }

  @Test
  void dismiss_showsTheContentAgain() {
    report(USER3, ReportTargetType.POST, post.getId());
    report(USER4, ReportTargetType.POST, post.getId());
    report(USER5, ReportTargetType.POST, post.getId());

    resolve(
            USER2,
            teamReports() + "/resolve",
            ReportTargetType.POST,
            post.getId(),
            ModerationAction.DISMISS)
        .statusCode(204);

    assertArrayEquals(new boolean[] {false, false}, dataService.teamEntityState(post));
    assertEquals(
        List.of(ReportStatus.DISMISSED, ReportStatus.DISMISSED, ReportStatus.DISMISSED),
        dataService.reportStatuses(ReportTargetType.POST, post.getId()));
    get(USER6, "/api/teams/" + team1Slug + "/posts/" + post.getSlug()).statusCode(200);
  }

  @Test
  void removeContent_ofAMember_isABadRequest() {
    report(USER3, ReportTargetType.MEMBER, user6.getId());

    resolve(
            USER1,
            teamReports() + "/resolve",
            ReportTargetType.MEMBER,
            user6.getId(),
            ModerationAction.REMOVE_CONTENT)
        .statusCode(400);
    assertEquals(
        List.of(ReportStatus.OPEN),
        dataService.reportStatuses(ReportTargetType.MEMBER, user6.getId()));
  }

  @Test
  void resolve_asAMember_isForbidden_andWithoutReports_is404() {
    report(USER3, ReportTargetType.POST, post.getId());

    resolve(
            USER6,
            teamReports() + "/resolve",
            ReportTargetType.POST,
            post.getId(),
            ModerationAction.REMOVE_CONTENT)
        .statusCode(403);
    resolve(
            USER1,
            teamReports() + "/resolve",
            ReportTargetType.ROUTE,
            post.getId(),
            ModerationAction.DISMISS)
        .statusCode(404);
  }
}
