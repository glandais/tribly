package fr.pedalons.api.comments;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pagination, sorting and thread loading on {@code GET …/comments}.
 *
 * <p>The endpoint answered with the whole tree and took no parameter at all before this; the first
 * test of the file is the one that matters most, because it pins that a caller passing nothing still
 * gets exactly that.
 */
@QuarkusTest
class CommentPaginationTest extends AbstractResourceTest {

  private Ride ride;
  private String ridePath;
  private final List<Comment> roots = new ArrayList<>();

  @BeforeEach
  void setUpFixture() {
    super.setUp();
    ride =
        dataService.createRide(
            team1,
            user1,
            "Commented ride",
            "commented-ride",
            Instant.now(),
            Visibility.PUBLIC,
            Status.PUBLISHED);
    ridePath = "/api/teams/" + team1Slug + "/rides/" + ride.getSlug() + "/comments";
    roots.clear();
    // 5 roots, the first one carrying 3 replies: 8 comments in total.
    for (int i = 0; i < 5; i++) {
      roots.add(dataService.createComment(user1, ride, "root " + i));
    }
    for (int i = 0; i < 3; i++) {
      dataService.createReply(user3, ride, roots.getFirst(), "reply " + i);
    }
  }

  @Test
  void listComments_withoutAnyParameter_shouldReturnTheWholeTreeAsBefore() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get(ridePath)
        .then()
        .statusCode(200)
        .body("items.size()", equalTo(5))
        .body("total", equalTo(8))
        .body("itemTotal", equalTo(5))
        .body("page", equalTo(0))
        .body("size", equalTo(5))
        .body("items[0].content", equalTo("root 0"))
        .body("items[0].replies.size()", equalTo(3))
        .body("items[0].replyCount", equalTo(3))
        .body("items[1].replyCount", equalTo(0));
  }

  @Test
  void listComments_withPageAndSize_shouldPaginateTheRootsNotTheFlatList() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("page", 0)
        .queryParam("size", 2)
        .when()
        .get(ridePath)
        .then()
        .statusCode(200)
        .body("items.size()", equalTo(2))
        // The first root keeps its three replies: a page cuts between threads, never inside one.
        .body("items[0].replies.size()", equalTo(3))
        .body("items[0].replyCount", equalTo(3))
        .body("itemTotal", equalTo(5))
        .body("total", equalTo(8))
        .body("page", equalTo(0))
        .body("size", equalTo(2));
  }

  @Test
  void listComments_secondPage_shouldReturnTheNextRoots() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("page", 2)
        .queryParam("size", 2)
        .when()
        .get(ridePath)
        .then()
        .statusCode(200)
        .body("items.size()", equalTo(1))
        .body("items[0].content", equalTo("root 4"))
        .body("itemTotal", equalTo(5))
        .body("page", equalTo(2));
  }

  @Test
  void listComments_withSizeAboveTheCap_shouldBeClampedByTheServer() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("size", 100_000)
        .when()
        .get(ridePath)
        .then()
        .statusCode(200)
        // The client asked for a hundred thousand; the answer says what it really got.
        .body("size", equalTo(100))
        .body("items.size()", equalTo(5));
  }

  @Test
  void listComments_sortedDesc_shouldReturnNewestRootsFirst() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("page", 0)
        .queryParam("size", 2)
        .queryParam("sort", "DESC")
        .when()
        .get(ridePath)
        .then()
        .statusCode(200)
        .body("items[0].content", equalTo("root 4"))
        .body("items[1].content", equalTo("root 3"));
  }

  @Test
  void listComments_withParentId_shouldReturnOnlyThatThreadsReplies() {
    String parentId = idOf(roots.getFirst());

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("parentId", parentId)
        .queryParam("page", 0)
        .queryParam("size", 2)
        .when()
        .get(ridePath)
        .then()
        .statusCode(200)
        .body("items.size()", equalTo(2))
        .body("items[0].parentId", equalTo(parentId))
        .body("items[0].content", equalTo("reply 0"))
        .body("itemTotal", equalTo(3))
        .body("total", equalTo(8));
  }

  @Test
  void listComments_withParentIdOfAnotherEntity_shouldReturnNothing() {
    Ride otherRide =
        dataService.createRide(
            team1,
            user1,
            "Other ride",
            "other-ride",
            Instant.now(),
            Visibility.PUBLIC,
            Status.PUBLISHED);
    Comment foreignRoot = dataService.createComment(user1, otherRide, "elsewhere");
    dataService.createReply(user1, otherRide, foreignRoot, "elsewhere reply");

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("parentId", idOf(foreignRoot))
        .when()
        .get(ridePath)
        .then()
        .statusCode(200)
        .body("items.size()", equalTo(0))
        .body("itemTotal", equalTo(0));
  }

  @Test
  void listComments_withAMalformedParentId_shouldReturn400() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .queryParam("parentId", "not-a-tsid!!")
        .when()
        .get(ridePath)
        .then()
        .statusCode(400);
  }

  @Test
  void listComments_paginated_withoutAuth_shouldReturn401() {
    given().queryParam("page", 0).queryParam("size", 2).when().get(ridePath).then().statusCode(401);
  }

  @Test
  void listComments_paginated_asNonMember_shouldReturn403() {
    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .queryParam("page", 0)
        .queryParam("size", 2)
        .when()
        .get(ridePath)
        .then()
        .statusCode(403);
  }

  /**
   * The tenancy test. Both domains hold a team and a ride with the very same slugs, and the same
   * person is a member on both sides — the setup a multi-tenant platform actually produces. Reading
   * the comments from the second domain must answer with the second domain's thread, never the
   * first's, even though the URL is character for character identical.
   */
  @Test
  void listComments_fromAnotherDomain_shouldNeverSeeTheFirstDomainsComments() {
    Domain other = dataService.createDomain("other.localhost", "Other", "http://other.localhost");
    User otherUser = dataService.createUser(other, EMAIL3, "Test User 3 elsewhere");
    // createTeam already enrols its creator as ADMIN.
    Team otherTeam =
        dataService.createTeam(other, otherUser, "Team 1", team1Slug, Visibility.PUBLIC);
    Ride otherRide =
        dataService.createRide(
            otherTeam,
            otherUser,
            "Commented ride",
            ride.getSlug(),
            Instant.now(),
            Visibility.PUBLIC,
            Status.PUBLISHED);
    dataService.createComment(otherUser, otherRide, "other domain only");

    given()
        .header("X-Forwarded-Host", "other.localhost")
        .auth()
        .oauth2(jwtService.generateAccessToken(otherUser))
        .when()
        .get(ridePath)
        .then()
        .statusCode(200)
        .body("items.size()", equalTo(1))
        .body("items[0].content", equalTo("other domain only"))
        .body("total", equalTo(1));
  }

  private static String idOf(Comment comment) {
    return TsidUtils.toString(comment.getId());
  }
}
