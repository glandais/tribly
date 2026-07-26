package fr.pedalons.api.publications;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.AssetType;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code ?view=compact} on the publication listings, plus the two fields that make it usable:
 * {@code excerpt} and {@code thumbnailUrl}.
 *
 * <p>The contract is strictly additive, so half of what is checked here is that the default
 * response did not move: a caller that does not send {@code view} must get exactly what it got
 * before the parameter existed, body and assets included.
 */
@QuarkusTest
class PublicationCompactViewTest extends AbstractResourceTest {

  private static final String ALL = "/api/publications";

  private static final String BODY =
      "# Sortie du dimanche\n\nOn part de la [place du village](https://example.com/place) a 9h,"
          + " **rythme tranquille**, retour vers midi.";

  private String teamList() {
    return "/api/teams/" + team1Slug + "/publications";
  }

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    Instant soon = Instant.now().plus(7, ChronoUnit.DAYS);

    Ride ride = dataService.createRide(team1, user1, "Public Ride", "public-ride", soon);
    dataService.setMarkdown(ride, BODY);
    dataService.attachAsset(ride, user1, AssetType.IMAGE, "photo.jpg");
    dataService.attachAsset(ride, user1, AssetType.RIDE_THUMBNAIL_LIGHT, "thumb-light.png");

    Post post = dataService.createPost(team1, user1, "Public Post", soon);
    dataService.setMarkdown(post, BODY);
    dataService.attachAsset(post, user1, AssetType.IMAGE, "cover.jpg");
  }

  private static String row(String name) {
    return "publications.find { it.name == '" + name + "' }";
  }

  // ---------------------------------------------------------------- nominal, default view

  @Test
  void listWithoutView_shouldStillCarryTheWholeBodyAndTheAssets() {
    given()
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body(row("Public Ride") + ".media.markdown", equalTo(BODY))
        .body(row("Public Ride") + ".media.assets.images", hasSize(1))
        .body(row("Public Post") + ".media.markdown", equalTo(BODY));
  }

  @Test
  void listWithoutView_shouldAlsoCarryTheNewSummaryFields() {
    given()
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body(row("Public Ride") + ".excerpt", not(emptyOrNullString()))
        .body(row("Public Ride") + ".thumbnailUrl", not(emptyOrNullString()))
        .body(row("Public Post") + ".thumbnailUrl", not(emptyOrNullString()));
  }

  // ---------------------------------------------------------------- compact

  @Test
  void listCompact_shouldDropTheBodyAndTheAssets() {
    given()
        .queryParam("view", "compact")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body(row("Public Ride") + ".media.markdown", equalTo(""))
        .body(row("Public Ride") + ".media.assets.images", hasSize(0))
        .body(row("Public Ride") + ".media.assets.attachments", hasSize(0))
        .body(row("Public Post") + ".media.markdown", equalTo(""))
        .body(row("Public Post") + ".media.assets.images", hasSize(0));
  }

  @Test
  void listCompact_shouldStillCarryEnoughToRenderTheCard() {
    given()
        .queryParam("view", "compact")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body(row("Public Ride") + ".name", equalTo("Public Ride"))
        .body(row("Public Ride") + ".excerpt", not(emptyOrNullString()))
        .body(row("Public Ride") + ".thumbnailUrl", not(emptyOrNullString()))
        .body(row("Public Post") + ".excerpt", not(emptyOrNullString()))
        .body(row("Public Post") + ".thumbnailUrl", not(emptyOrNullString()));
  }

  @Test
  void view_shouldBeCaseInsensitive() {
    given()
        .queryParam("view", "COMPACT")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body(row("Public Ride") + ".media.markdown", equalTo(""));
  }

  @Test
  void viewFull_shouldBeTheDefaultBehaviour() {
    given()
        .queryParam("view", "FULL")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body(row("Public Ride") + ".media.markdown", equalTo(BODY));
  }

  @Test
  void teamListing_shouldAcceptViewToo() {
    given()
        .queryParam("view", "compact")
        .when()
        .get(teamList())
        .then()
        .statusCode(200)
        .body(row("Public Ride") + ".media.markdown", equalTo(""))
        .body(row("Public Ride") + ".excerpt", not(emptyOrNullString()));
  }

  // ---------------------------------------------------------------- excerpt content

  @Test
  void excerpt_shouldBePlainTextWithNoMarkdownLeftIn() {
    String excerpt =
        given()
            .queryParam("view", "compact")
            .when()
            .get(ALL)
            .then()
            .statusCode(200)
            .extract()
            .path(row("Public Ride") + ".excerpt");

    assertNotNull(excerpt);
    // The link label survives, the URL and the syntax do not.
    assertTrue(excerpt.contains("place du village"), excerpt);
    for (String syntax : new String[] {"#", "**", "[", "]", "(", ")", "https://"}) {
      assertFalse(excerpt.contains(syntax), "excerpt still holds '" + syntax + "': " + excerpt);
    }
  }

  // ---------------------------------------------------------------- visibility

  @Test
  void listCompact_anonymous_shouldNotSeeAPrivateTeamsPublications() {
    Ride secret =
        dataService.createRide(
            team2, user1, "Secret Ride", "secret-ride", Instant.now().plus(3, ChronoUnit.DAYS));
    dataService.setMarkdown(secret, BODY);

    given()
        .queryParam("view", "compact")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body("publications.name", not(hasItem("Secret Ride")))
        .body("publications.name", hasItem("Public Ride"));
  }

  @Test
  void listCompact_member_shouldSeeTheirPrivateTeamsPublications() {
    Ride secret =
        dataService.createRide(
            team2, user1, "Secret Ride", "secret-ride", Instant.now().plus(3, ChronoUnit.DAYS));
    dataService.setMarkdown(secret, BODY);

    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .queryParam("view", "compact")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body("publications.name", hasItem("Secret Ride"))
        .body(row("Secret Ride") + ".media.markdown", equalTo(""))
        .body(row("Secret Ride") + ".excerpt", not(emptyOrNullString()));
  }

  @Test
  void listCompact_shouldNotReachIntoAnotherDomain() {
    Domain other = dataService.createDomain("other.localhost", "Other", "http://other.localhost");
    User otherUser = dataService.createUser(other, EMAIL1, "Other User 1");
    Team otherTeam =
        dataService.createTeam(other, otherUser, "Foreign Team", "foreign-team", Visibility.PUBLIC);
    Ride foreign =
        dataService.createRide(
            otherTeam,
            otherUser,
            "Foreign Ride",
            "foreign-ride",
            Instant.now().plus(2, ChronoUnit.DAYS));
    dataService.setMarkdown(foreign, BODY);

    given()
        .queryParam("view", "compact")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .body("publications.name", not(hasItem("Foreign Ride")))
        .body("total", equalTo(2));
  }

  // ---------------------------------------------------------------- caching

  @Test
  void listCompact_shouldStayOutOfSharedCaches() {
    given()
        .queryParam("view", "compact")
        .when()
        .get(ALL)
        .then()
        .statusCode(200)
        .header("Cache-Control", containsString("private"))
        .header("Cache-Control", containsString("no-store"));
  }
}
