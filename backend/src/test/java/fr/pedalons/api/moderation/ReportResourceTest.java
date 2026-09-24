package fr.pedalons.api.moderation;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ad.Ad;
import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.post.Post;
import fr.pedalons.dto.moderation.request.ReportRequest;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.ReportReason;
import fr.pedalons.enums.ReportStatus;
import fr.pedalons.enums.ReportTargetType;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code POST /api/reports}. Fixture: team1 is public, with user1 (admin), user2 (organizer) and
 * user3 (member); team2 is private, same members; user4 and user5 belong to neither.
 */
@QuarkusTest
class ReportResourceTest extends AbstractResourceTest {

  private final Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);

  private Post post;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    post = dataService.createPost(team1, user2, "Compte rendu", yesterday, Visibility.PUBLIC);
  }

  private ValidatableResponse report(
      String user, String teamSlug, ReportTargetType type, Long targetId, @Nullable String msg) {
    return given()
        .auth()
        .oauth2(getAccessToken(user))
        .contentType("application/json")
        .body(
            new ReportRequest(
                teamSlug, type, TsidUtils.toString(targetId), ReportReason.HARASSMENT, msg))
        .when()
        .post("/api/reports")
        .then();
  }

  private ValidatableResponse report(String user, ReportTargetType type, Long targetId) {
    return report(user, team1Slug, type, targetId, null);
  }

  private ValidatableResponse listPosts(String user) {
    return given()
        .auth()
        .oauth2(getAccessToken(user))
        .queryParam("type", "POST")
        .when()
        .get("/api/teams/" + team1Slug + "/publications")
        .then()
        .statusCode(200);
  }

  // ------------------------------------------------------------------ publications

  @Test
  void reportPost_hidesItFromTheReportersListsOnly() {
    report(USER3, ReportTargetType.POST, post.getId()).statusCode(204);

    assertEquals(
        List.of(ReportStatus.OPEN),
        dataService.reportStatuses(ReportTargetType.POST, post.getId()));
    listPosts(USER3).body("publications.slug", not(hasItem(post.getSlug())));
    listPosts(USER1).body("publications.slug", hasItem(post.getSlug()));
    // The detail stays reachable by link.
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/posts/" + post.getSlug())
        .then()
        .statusCode(200);
  }

  @Test
  void reportPost_isIdempotent() {
    report(USER3, team1Slug, ReportTargetType.POST, post.getId(), "Premier").statusCode(204);
    report(USER3, team1Slug, ReportTargetType.POST, post.getId(), "Second").statusCode(204);

    assertEquals(1, dataService.reportCount());
  }

  @Test
  void reportPublicPost_byANonMember_isAllowed() {
    report(USER4, ReportTargetType.POST, post.getId()).statusCode(204);
  }

  @Test
  void reportPost_ofAPrivateTeam_byANonMember_is404() {
    Post hidden = dataService.createPost(team2, user2, "Interne", yesterday, Visibility.TEAM);

    report(USER4, team2Slug, ReportTargetType.POST, hidden.getId(), null).statusCode(404);
    assertEquals(0, dataService.reportCount());
  }

  @Test
  void reportPost_throughAnotherTeam_is404() {
    report(USER3, team2Slug, ReportTargetType.POST, post.getId(), null).statusCode(404);
  }

  @Test
  void reportUnknownTarget_is404() {
    report(USER3, ReportTargetType.RIDE, post.getId()).statusCode(404);
    report(USER3, ReportTargetType.POST, 123456789L).statusCode(404);
  }

  @Test
  void reportOwnPost_isReportSelf() {
    report(USER2, ReportTargetType.POST, post.getId())
        .statusCode(400)
        .body("code", equalTo("REPORT_SELF"));
  }

  @Test
  void report_withoutAuth_is401() {
    given()
        .contentType("application/json")
        .body(
            new ReportRequest(
                team1Slug,
                ReportTargetType.POST,
                TsidUtils.toString(post.getId()),
                ReportReason.SPAM,
                null))
        .when()
        .post("/api/reports")
        .then()
        .statusCode(401);
  }

  // ------------------------------------------------------------------ ads

  @Test
  void reportAd_asMember_hidesItFromTheirClassifieds() {
    Ad ad = dataService.createAd(team1, user2, "Vélo à vendre", AdType.SALE);

    report(USER3, ReportTargetType.AD, ad.getId()).statusCode(204);

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get("/api/teams/" + team1Slug + "/classifieds")
        .then()
        .statusCode(200)
        .body("ads.slug", not(hasItem(ad.getSlug())));
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get("/api/teams/" + team1Slug + "/classifieds")
        .then()
        .statusCode(200)
        .body("ads.slug", hasItem(ad.getSlug()));
  }

  @Test
  void reportAd_byANonMember_is404() {
    Ad ad = dataService.createAd(team1, user2, "Vélo à vendre", AdType.SALE);

    report(USER4, ReportTargetType.AD, ad.getId()).statusCode(404);
  }

  // ------------------------------------------------------------------ comments

  @Test
  void reportComment_hidesItFromTheReportersThread() {
    Comment comment = dataService.createComment(user2, post, "Un commentaire");

    report(USER3, ReportTargetType.COMMENT, comment.getId()).statusCode(204);

    String path = "/api/teams/" + team1Slug + "/posts/" + post.getSlug() + "/comments";
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body("items.id", not(hasItem(TsidUtils.toString(comment.getId()))))
        // The totals stay those of the database.
        .body("total", equalTo(1));
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body("items.id", hasItem(TsidUtils.toString(comment.getId())));
  }

  @Test
  void reportComment_byANonMember_is404() {
    Comment comment = dataService.createComment(user2, post, "Un commentaire");

    report(USER4, ReportTargetType.COMMENT, comment.getId()).statusCode(404);
  }

  @Test
  void reportOwnComment_isReportSelf() {
    Comment comment = dataService.createComment(user3, post, "Le mien");

    report(USER3, ReportTargetType.COMMENT, comment.getId())
        .statusCode(400)
        .body("code", equalTo("REPORT_SELF"));
  }

  // ------------------------------------------------------------------ members

  @Test
  void reportMember_betweenMembers_isAllowed() {
    report(USER3, ReportTargetType.MEMBER, user2.getId()).statusCode(204);

    assertEquals(
        List.of(ReportStatus.OPEN),
        dataService.reportStatuses(ReportTargetType.MEMBER, user2.getId()));
  }

  @Test
  void reportMember_needsBothToBeMembers() {
    // The reporter is not a member.
    report(USER4, ReportTargetType.MEMBER, user2.getId()).statusCode(404);
    // The reported user is not a member.
    report(USER3, ReportTargetType.MEMBER, user4.getId()).statusCode(404);
  }

  @Test
  void reportMyself_isReportSelf() {
    report(USER3, ReportTargetType.MEMBER, user3.getId())
        .statusCode(400)
        .body("code", equalTo("REPORT_SELF"));
  }
}
