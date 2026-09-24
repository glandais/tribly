package fr.pedalons.api.users;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ad.Ad;
import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code /api/users/me/blocks}, and what a block hides. Fixture: team1 is public, with user1
 * (admin), user2 (organizer) and user3 (member). Here user3 blocks user2, who authors a post, an
 * ad, a ride and a comment.
 */
@QuarkusTest
class UserBlockResourceTest extends AbstractResourceTest {

  private final Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);

  private Post post;
  private Ad ad;
  private Ride ride;
  private Comment comment;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    post = dataService.createPost(team1, user2, "Billet", yesterday, Visibility.PUBLIC);
    ad = dataService.createAd(team1, user2, "Roue à vendre", AdType.SALE);
    ride =
        dataService.createRide(
            team1, user2, "Sortie", "sortie", Instant.now().plus(3, ChronoUnit.DAYS));
    comment = dataService.createComment(user2, post, "Un avis");
  }

  private String blocksPath(User user) {
    return "/api/users/me/blocks/" + TsidUtils.toString(user.getId());
  }

  private ValidatableResponse block(String user, User blocked) {
    return given().auth().oauth2(getAccessToken(user)).when().put(blocksPath(blocked)).then();
  }

  private ValidatableResponse unblock(String user, User blocked) {
    return given().auth().oauth2(getAccessToken(user)).when().delete(blocksPath(blocked)).then();
  }

  private ValidatableResponse get(String user, String path) {
    return given().auth().oauth2(getAccessToken(user)).when().get(path).then().statusCode(200);
  }

  private ValidatableResponse publications(String user) {
    return get(user, "/api/teams/" + team1Slug + "/publications");
  }

  private ValidatableResponse classifieds(String user) {
    return get(user, "/api/teams/" + team1Slug + "/classifieds");
  }

  private ValidatableResponse comments(String user) {
    return get(user, "/api/teams/" + team1Slug + "/posts/" + post.getSlug() + "/comments");
  }

  @Test
  void block_isListed_andIdempotent() {
    block(USER3, user2).statusCode(204);
    block(USER3, user2).statusCode(204);

    get(USER3, "/api/users/me/blocks")
        .body("users", hasSize(1))
        .body("users[0].id", equalTo(TsidUtils.toString(user2.getId())))
        .body("users[0].displayName", equalTo("Test User 2"));
  }

  @Test
  void blocker_noLongerSeesThePostsAdsAndCommentsOfTheBlocked() {
    block(USER3, user2).statusCode(204);

    publications(USER3).body("publications.slug", not(hasItem(post.getSlug())));
    classifieds(USER3).body("ads.slug", not(hasItem(ad.getSlug())));
    comments(USER3)
        .body("items", hasSize(0))
        // The totals stay those of the database.
        .body("total", equalTo(1));
    // Rides are the team's organisation: they stay.
    publications(USER3).body("publications.slug", hasItem(ride.getSlug()));
    // The detail stays reachable by link.
    get(USER3, "/api/teams/" + team1Slug + "/posts/" + post.getSlug());
  }

  @Test
  void blockedMember_seesNothingChange() {
    block(USER3, user2).statusCode(204);

    publications(USER2).body("publications.slug", hasItem(post.getSlug()));
    classifieds(USER2).body("ads.slug", hasItem(ad.getSlug()));
    comments(USER2).body("items", hasSize(1));
    get(USER2, "/api/users/me/blocks").body("users", hasSize(0));
    // Nor does anyone else.
    publications(USER1).body("publications.slug", hasItem(post.getSlug()));
  }

  @Test
  void blockedAuthorsComment_withAnsweredThread_staysAsATombstone() {
    dataService.createReply(user1, post, comment, "Une réponse");
    block(USER3, user2).statusCode(204);

    comments(USER3)
        .body("items", hasSize(1))
        .body("items[0].deleted", equalTo(true))
        .body("items[0].content", equalTo(""))
        .body("items[0].replies", hasSize(1))
        .body("items[0].replies[0].content", equalTo("Une réponse"));
  }

  @Test
  void blockedAuthorsReply_isRemoved() {
    Comment root = dataService.createComment(user1, post, "Question");
    dataService.createReply(user2, post, root, "Réponse du bloqué");
    block(USER3, user2).statusCode(204);

    comments(USER3)
        .body("items", hasSize(1))
        .body("items[0].id", equalTo(TsidUtils.toString(root.getId())))
        .body("items[0].replies", hasSize(0));
  }

  @Test
  void unblock_bringsEverythingBack() {
    block(USER3, user2).statusCode(204);
    unblock(USER3, user2).statusCode(204);
    unblock(USER3, user2).statusCode(204);

    assertFalse(dataService.isBlocked(user3, user2));
    publications(USER3).body("publications.slug", hasItem(post.getSlug()));
    classifieds(USER3).body("ads.slug", hasItem(ad.getSlug()));
    comments(USER3).body("items", hasSize(1));
    get(USER3, "/api/users/me/blocks").body("users", hasSize(0));
  }

  @Test
  void blockMyself_isBlockSelf() {
    block(USER3, user3).statusCode(400).body("code", equalTo("BLOCK_SELF"));
  }

  @Test
  void blockUnknownOrDeletedUser_is404() {
    User other = dataService.createUser("gone@example.com", "Gone");
    dataService.deleteUser(other);

    block(USER3, other).statusCode(404);
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .put("/api/users/me/blocks/" + TsidUtils.toString(123456789L))
        .then()
        .statusCode(404);
  }

  @Test
  void block_withoutAuth_is401() {
    given().when().put(blocksPath(user2)).then().statusCode(401);
    assertFalse(dataService.isBlocked(user3, user2));
  }
}
