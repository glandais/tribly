package fr.pedalons.api.tags;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ridetemplate.RideTemplate;
import fr.pedalons.domain.tag.Tag;
import fr.pedalons.dto.tags.request.TagCreateRequest;
import fr.pedalons.dto.tags.request.TagUpdateRequest;
import fr.pedalons.enums.TagColor;
import fr.pedalons.enums.TagTarget;
import fr.pedalons.service.tag.TagService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A team's tag vocabulary (docs/LEDGER_*.md API-59, docs/plans/archive/2026-10-01-tags.md): read by
 * whoever sees the team, written by its admins only (D4), unique per team and kind regardless of
 * case (D17), bounded (D16), and detached everywhere on deletion (D11).
 */
@QuarkusTest
class TagResourceTest extends AbstractResourceTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  private String tagsPath(String teamSlug) {
    return "/api/teams/" + teamSlug + "/tags";
  }

  private ValidatableResponse create(String user, TagTarget type, String label) {
    return given()
        .auth()
        .oauth2(getAccessToken(user))
        .contentType("application/json")
        .body(new TagCreateRequest(type, label, TagColor.GREEN))
        .when()
        .post(tagsPath(team1Slug))
        .then();
  }

  private ValidatableResponse update(String user, Tag tag, TagUpdateRequest request) {
    return given()
        .auth()
        .oauth2(getAccessToken(user))
        .contentType("application/json")
        .body(request)
        .when()
        .patch(tagsPath(team1Slug) + "/" + TsidUtils.toString(tag.getId()))
        .then();
  }

  private ValidatableResponse delete(String user, String teamSlug, Tag tag) {
    return given()
        .auth()
        .oauth2(getAccessToken(user))
        .when()
        .delete(tagsPath(teamSlug) + "/" + TsidUtils.toString(tag.getId()))
        .then();
  }

  // ─── Reading ──────────────────────────────────────────────────────────────

  @Test
  void list_anonymousOnAPublicTeam_isSortedByLabel_caseInsensitively_withUsage() {
    Tag gravel = dataService.createTag(team1, user1, TagTarget.ROUTE, "gravel");
    dataService.createTag(team1, user1, TagTarget.ROUTE, "Boucle");
    dataService.createTag(team1, user1, TagTarget.ROUTE, "Col");
    dataService.tagContent(dataService.createRoute(team1, user1, "Route A"), gravel);
    dataService.tagContent(dataService.createRoute(team1, user1, "Route B"), gravel);

    given()
        .queryParam("type", "ROUTE")
        .when()
        .get(tagsPath(team1Slug))
        .then()
        .statusCode(200)
        .body("label", contains("Boucle", "Col", "gravel"))
        .body("[2].usageCount", equalTo(2))
        .body("[0].usageCount", equalTo(0))
        .body("[0].type", equalTo("ROUTE"))
        .body("[0].color", equalTo("INDIGO"));
  }

  @Test
  void list_byType_keepsOnlyThatKind_andWithoutType_returnsThemAll() {
    dataService.createTag(team1, user1, TagTarget.ROUTE, "Gravel");
    dataService.createTag(team1, user1, TagTarget.RIDE, "Gravel");
    dataService.createTag(team1, user1, TagTarget.AD, "Vélo");
    dataService.createTag(team2, user1, TagTarget.RIDE, "Other team");

    given()
        .queryParam("type", "RIDE")
        .when()
        .get(tagsPath(team1Slug))
        .then()
        .statusCode(200)
        .body("$", hasSize(1))
        .body("[0].type", equalTo("RIDE"));
    given().when().get(tagsPath(team1Slug)).then().statusCode(200).body("$", hasSize(3));
  }

  @Test
  void list_ofAPrivateTeam_isDeniedToANonMember_andOpenToAMember() {
    dataService.createTag(team2, user1, TagTarget.POST, "Interne");

    given()
        .auth()
        .oauth2(getAccessToken(USER4))
        .when()
        .get(tagsPath(team2Slug))
        .then()
        .statusCode(403);
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .when()
        .get(tagsPath(team2Slug))
        .then()
        .statusCode(200)
        .body("label", contains("Interne"));
  }

  // ─── Rights (D4) ──────────────────────────────────────────────────────────

  @Test
  void create_asAdmin_trimsTheLabel_andStartsUnused() {
    create(USER1, TagTarget.RIDE, "  Sortie longue  ")
        .statusCode(201)
        .body("id", notNullValue())
        .body("label", equalTo("Sortie longue"))
        .body("color", equalTo("GREEN"))
        .body("type", equalTo("RIDE"))
        .body("usageCount", equalTo(0));
  }

  @Test
  void create_update_delete_areDeniedToOrganizersAndMembers() {
    Tag tag = dataService.createTag(team1, user1, TagTarget.RIDE, "Existing");

    for (String user : List.of(USER2, USER3, USER4)) {
      create(user, TagTarget.RIDE, "Nope").statusCode(403);
      update(user, tag, new TagUpdateRequest("Renamed", null)).statusCode(403);
      delete(user, team1Slug, tag).statusCode(403);
    }
    given()
        .contentType("application/json")
        .body(new TagCreateRequest(TagTarget.RIDE, "Anonymous", TagColor.GRAY))
        .when()
        .post(tagsPath(team1Slug))
        .then()
        .statusCode(401);
    assertEquals(List.of("Existing"), labels(TagTarget.RIDE));
  }

  // ─── Bounds (D16) and uniqueness (D17) ────────────────────────────────────

  @Test
  void blankOrLongerThan32AfterTheTrim_isTagLabelInvalid() {
    create(USER1, TagTarget.POST, "   ").statusCode(400).body("code", equalTo("TAG_LABEL_INVALID"));
    create(USER1, TagTarget.POST, "x".repeat(33))
        .statusCode(400)
        .body("code", equalTo("TAG_LABEL_INVALID"));
    create(USER1, TagTarget.POST, "x".repeat(32)).statusCode(201);
    // The 32 characters are counted once trimmed: padding does not make a label too long.
    create(USER1, TagTarget.POST, "  " + "y".repeat(32) + "  ").statusCode(201);
    Tag tag = dataService.createTag(team1, user1, TagTarget.POST, "Valide");

    update(USER1, tag, new TagUpdateRequest("   ", null))
        .statusCode(400)
        .body("code", equalTo("TAG_LABEL_INVALID"));
    update(USER1, tag, new TagUpdateRequest("x".repeat(33), null))
        .statusCode(400)
        .body("code", equalTo("TAG_LABEL_INVALID"));
    update(USER1, tag, new TagUpdateRequest(" " + "z".repeat(32) + " ", null)).statusCode(200);
  }

  @Test
  void create_theSameLabelInAnotherCase_isTaken_butNotInAnotherKindNorAnotherTeam() {
    create(USER1, TagTarget.ROUTE, "Gravel").statusCode(201);

    create(USER1, TagTarget.ROUTE, " gRAVEL ")
        .statusCode(409)
        .body("code", equalTo("TAG_LABEL_TAKEN"));
    create(USER1, TagTarget.RIDE, "gravel").statusCode(201);
    dataService.createTag(team2, user1, TagTarget.ROUTE, "gravel");
    assertEquals(List.of("Gravel"), labels(TagTarget.ROUTE));
  }

  @Test
  void create_beyond100PerTeamAndKind_isTagLimitReached() {
    for (int i = 0; i < TagService.MAX_TAGS_PER_TEAM_AND_TYPE; i++) {
      dataService.createTag(team1, user1, TagTarget.AD, "Tag " + i);
    }

    create(USER1, TagTarget.AD, "One too many")
        .statusCode(400)
        .body("code", equalTo("TAG_LIMIT_REACHED"));
    // The bound is per kind.
    create(USER1, TagTarget.TRIP, "Still fine").statusCode(201);
  }

  @Test
  void update_renamesAndRecolours_andKeepsItsUsage() {
    Tag tag = dataService.createTag(team1, user1, TagTarget.POST, "Compte rendu");
    dataService.tagContent(dataService.createPost(team1, user1, "Post", Instant.now()), tag);

    update(USER1, tag, new TagUpdateRequest(" Récit ", TagColor.ORANGE))
        .statusCode(200)
        .body("label", equalTo("Récit"))
        .body("color", equalTo("ORANGE"))
        .body("usageCount", equalTo(1));
    // An absent field is left alone.
    update(USER1, tag, new TagUpdateRequest(null, TagColor.TEAL))
        .statusCode(200)
        .body("label", equalTo("Récit"))
        .body("color", equalTo("TEAL"));
  }

  @Test
  void update_toAnotherTagsLabel_inAnyCase_isTaken_butACaseChangeOfItsOwnIsARename() {
    dataService.createTag(team1, user1, TagTarget.POST, "Météo");
    Tag tag = dataService.createTag(team1, user1, TagTarget.POST, "gravel");

    update(USER1, tag, new TagUpdateRequest("MÉTÉO", null))
        .statusCode(409)
        .body("code", equalTo("TAG_LABEL_TAKEN"));
    update(USER1, tag, new TagUpdateRequest("Gravel", null))
        .statusCode(200)
        .body("label", equalTo("Gravel"));
  }

  @Test
  void update_orDelete_aTagOfAnotherTeam_isNotFound() {
    Tag foreign = dataService.createTag(team2, user1, TagTarget.POST, "Foreign");

    // user1 administers both teams: the tag is only reachable under its own.
    update(USER1, foreign, new TagUpdateRequest("Hijacked", null)).statusCode(404);
    delete(USER1, team1Slug, foreign).statusCode(404);
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .when()
        .delete(tagsPath(team1Slug) + "/not-a-tsid")
        .then()
        .statusCode(404);
  }

  // ─── Deletion (D11) ───────────────────────────────────────────────────────

  @Test
  void delete_detachesTheTagEverywhere_andAnswersTheUsageItAnnounced() {
    Tag tag = dataService.createTag(team1, user1, TagTarget.RIDE, "Nocturne");
    Tag kept = dataService.createTag(team1, user1, TagTarget.RIDE, "Gardé");
    var ride =
        dataService.createRide(team1, user1, "Ride", "ride", Instant.now().plusSeconds(3600));
    var trashed =
        dataService.createRide(team1, user1, "Trashed", "trashed", Instant.now().plusSeconds(60));
    RideTemplate template = dataService.createRideTemplate(team1, user1, "Template", "template");
    dataService.tagContent(ride, tag, kept);
    dataService.tagContent(trashed, tag);
    dataService.tagTemplate(template, tag);
    dataService.deleteRide(trashed);

    given()
        .queryParam("type", "RIDE")
        .when()
        .get(tagsPath(team1Slug))
        .then()
        .body("find { it.label == 'Nocturne' }.usageCount", equalTo(2));

    // The ride and the template; the trashed ride's link goes too, uncounted.
    delete(USER1, team1Slug, tag).statusCode(200).body("detachedCount", equalTo(2));

    assertFalse(dataService.tagExists(tag.getId()));
    assertEquals(0, dataService.countTagLinks(tag.getId()));
    assertEquals(List.of("Gardé"), dataService.tagLabelsOf(ride.getId()));
    delete(USER1, team1Slug, tag).statusCode(404);
  }

  @Test
  void delete_ofAPostTag_leavesThePostInPlace() {
    Tag tag = dataService.createTag(team1, user1, TagTarget.POST, "Annonce");
    Post post = dataService.createPost(team1, user1, "Post", Instant.now());
    dataService.tagContent(post, tag);

    delete(USER1, team1Slug, tag).statusCode(200).body("detachedCount", equalTo(1));

    given()
        .when()
        .get("/api/teams/" + team1Slug + "/posts/" + post.getSlug())
        .then()
        .statusCode(200)
        .body("tags", hasSize(0));
  }

  private List<String> labels(TagTarget type) {
    return dataService.tagsOf(team1.getId(), type).stream().map(Tag::getLabel).toList();
  }
}
