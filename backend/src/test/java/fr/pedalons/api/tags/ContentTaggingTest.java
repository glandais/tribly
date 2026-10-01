package fr.pedalons.api.tags;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ridetemplate.RideTemplate;
import fr.pedalons.domain.tag.Tag;
import fr.pedalons.dto.ads.request.AdRequest;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.posts.request.PostRequest;
import fr.pedalons.dto.rides.request.GroupRequest;
import fr.pedalons.dto.rides.request.RideRequest;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.TagTarget;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.tag.TagService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tagging a content through its own create/update request (docs/LEDGER_*.md API-59): exactly the
 * content's edit rights (D5), only tags of its team and of its kind (D3), at most 10 (D16), and
 * {@code tagIds} omitted leaves the tags alone while {@code []} removes them.
 */
@QuarkusTest
class ContentTaggingTest extends AbstractResourceTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  private static String id(Tag tag) {
    return TsidUtils.toString(tag.getId());
  }

  private static PostRequest post(String name, @Nullable List<String> tagIds) {
    return new PostRequest(
        name,
        MediaDto.builder().markdown("Contenu").build(),
        Instant.now().minus(1, ChronoUnit.HOURS),
        Status.PUBLISHED,
        Visibility.PUBLIC,
        null,
        null,
        tagIds);
  }

  private ValidatableResponse createPost(String user, PostRequest request) {
    return given()
        .auth()
        .oauth2(getAccessToken(user))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/posts")
        .then();
  }

  private ValidatableResponse updatePost(String slug, PostRequest request) {
    return given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .put("/api/teams/" + team1Slug + "/posts/" + slug)
        .then();
  }

  // ─── Setting and clearing ─────────────────────────────────────────────────

  @Test
  void createPost_withTags_answersThemByLabel_andCountsADuplicateOnce() {
    Tag sortie = dataService.createTag(team1, user1, TagTarget.POST, "sortie");
    Tag annonce = dataService.createTag(team1, user1, TagTarget.POST, "Annonce");

    createPost(USER1, post("Tagged", List.of(id(sortie), id(annonce), id(sortie))))
        .statusCode(201)
        .body("tags.label", contains("Annonce", "sortie"))
        .body("tags[0].id", equalTo(id(annonce)))
        .body("tags[0].color", equalTo("INDIGO"));
  }

  @Test
  void updatePost_withoutTagIds_keepsThem_andWithAnEmptyList_removesThemAll() {
    Tag a = dataService.createTag(team1, user1, TagTarget.POST, "A");
    Tag b = dataService.createTag(team1, user1, TagTarget.POST, "B");
    String slug =
        createPost(USER1, post("Post", List.of(id(a), id(b))))
            .statusCode(201)
            .extract()
            .path("slug");

    // An older client that does not know the field must not wipe the tags.
    updatePost(slug, post("Post renamed", null))
        .statusCode(200)
        .body("tags.label", contains("A", "B"));
    updatePost(slug, post("Post renamed", List.of(id(b))))
        .statusCode(200)
        .body("tags.label", contains("B"));
    updatePost(slug, post("Post renamed", List.of())).statusCode(200).body("tags", hasSize(0));
  }

  @Test
  void anOrganizerEditingAPost_tagsIt_withoutBeingAdmin() {
    Tag tag = dataService.createTag(team1, user1, TagTarget.POST, "Récit");

    createPost(USER2, post("By the organizer", List.of(id(tag))))
        .statusCode(201)
        .body("tags.label", contains("Récit"));
  }

  @Test
  void aMemberCreatingAnAd_tagsIt_withTheTeamsAdTags() {
    Tag tag = dataService.createTag(team1, user1, TagTarget.AD, "Vélo route");

    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .body(
            new AdRequest(
                "Cadre carbone",
                MediaDto.builder().markdown("Peu servi").build(),
                Status.PUBLISHED,
                AdType.SALE,
                BigDecimal.valueOf(500),
                null,
                null,
                null,
                List.of(id(tag))))
        .when()
        .post("/api/teams/" + team1Slug + "/classifieds")
        .then()
        .statusCode(201)
        .body("tags.label", contains("Vélo route"));
  }

  // ─── Refusals (D3, D16) ───────────────────────────────────────────────────

  @Test
  void aTagOfAnotherKind_isTagInvalid() {
    Tag rideTag = dataService.createTag(team1, user1, TagTarget.RIDE, "Nocturne");

    createPost(USER1, post("Wrong kind", List.of(id(rideTag))))
        .statusCode(400)
        .body("code", equalTo("TAG_INVALID"));
  }

  @Test
  void aTagOfAnotherTeam_isTagInvalid_evenForAnAdminOfBoth() {
    Tag foreign = dataService.createTag(team2, user1, TagTarget.POST, "Ailleurs");

    createPost(USER1, post("Wrong team", List.of(id(foreign))))
        .statusCode(400)
        .body("code", equalTo("TAG_INVALID"));
  }

  @Test
  void anUnknownOrMalformedId_isTagInvalid_andNothingIsStored() {
    Tag tag = dataService.createTag(team1, user1, TagTarget.POST, "Bon");
    String slug = createPost(USER1, post("Post", List.of(id(tag)))).extract().path("slug");
    String unknown = TsidUtils.toString(tag.getId() + 1);

    updatePost(slug, post("Post", List.of(id(tag), unknown)))
        .statusCode(400)
        .body("code", equalTo("TAG_INVALID"));
    updatePost(slug, post("Post", List.of("not-a-tsid")))
        .statusCode(400)
        .body("code", equalTo("TAG_INVALID"));
    given()
        .when()
        .get("/api/teams/" + team1Slug + "/posts/" + slug)
        .then()
        .body("tags.label", contains("Bon"));
  }

  @Test
  void moreThan10Tags_isTooManyTags_but10IsFine() {
    List<String> ids = new ArrayList<>();
    for (int i = 0; i <= TagService.MAX_TAGS_PER_CONTENT; i++) {
      ids.add(id(dataService.createTag(team1, user1, TagTarget.POST, "Tag " + i)));
    }

    createPost(USER1, post("Too many", ids)).statusCode(400).body("code", equalTo("TOO_MANY_TAGS"));
    createPost(USER1, post("Ten", ids.subList(0, TagService.MAX_TAGS_PER_CONTENT)))
        .statusCode(201)
        .body("tags", hasSize(TagService.MAX_TAGS_PER_CONTENT));
  }

  // ─── Ride templates (D14) ─────────────────────────────────────────────────

  @Test
  void aTemplatesTags_areAnswered_andARideCreatedWithThem_carriesThem() {
    Tag tag = dataService.createTag(team1, user1, TagTarget.RIDE, "Gravel");
    RideTemplate template = dataService.createRideTemplate(team1, user1, "Samedi", "samedi");
    dataService.tagTemplate(template, tag);

    List<String> templateTagIds =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .when()
            .get("/api/teams/" + team1Slug + "/ride-templates/samedi")
            .then()
            .statusCode(200)
            .body("tags.label", contains("Gravel"))
            .extract()
            .path("tags.id");

    // The web form copies the template into an ordinary RideRequest, its tags included.
    String rideSlug =
        given()
            .auth()
            .oauth2(getAccessToken(USER1))
            .contentType("application/json")
            .body(
                new RideRequest(
                    "Samedi 4",
                    MediaDto.builder().build(),
                    Instant.now().plus(3, ChronoUnit.DAYS),
                    Status.PUBLISHED,
                    Visibility.PUBLIC,
                    null,
                    null,
                    null,
                    null,
                    List.of(new GroupRequest(null, "A", null, null, null, null)),
                    templateTagIds))
            .when()
            .post("/api/teams/" + team1Slug + "/rides")
            .then()
            .statusCode(201)
            .extract()
            .path("slug");

    given()
        .when()
        .get("/api/teams/" + team1Slug + "/rides/" + rideSlug)
        .then()
        .statusCode(200)
        .body("tags.label", contains("Gravel"));
  }
}
