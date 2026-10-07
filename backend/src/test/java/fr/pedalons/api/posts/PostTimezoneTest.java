package fr.pedalons.api.posts;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.dto.common.EventDateTime;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.posts.request.PostRequest;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import java.time.Instant;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** A post has no place: its wall times are the team's (docs/LEDGER_*.md API-60, plan §4). */
@QuarkusTest
class PostTimezoneTest extends AbstractResourceTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    dataService.setTeamModules(team1, true, true);
  }

  private JsonPath create(PostRequest request) {
    return given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/teams/" + team1Slug + "/posts")
        .then()
        .statusCode(201)
        .extract()
        .jsonPath();
  }

  @Test
  void aWallTime_isReadInTheTeamsZone() {
    dataService.setTeamTimezone(team1, "Asia/Tokyo");

    JsonPath post =
        create(
            new PostRequest(
                "Billet",
                MediaDto.builder().build(),
                EventDateTime.local(LocalDateTime.parse("2030-06-02T08:00:00")),
                Status.DRAFT,
                Visibility.PUBLIC,
                EventDateTime.local(LocalDateTime.parse("2030-06-01T18:00:00")),
                null,
                null));

    assertEquals("Asia/Tokyo", post.getString("timezone"));
    assertEquals("2030-06-01T23:00:00Z", post.getString("dateTime"));
    assertEquals(
        Instant.parse("2030-06-01T09:00:00Z"),
        dataService.getPublishAt(TsidUtils.toLong(post.getString("id"))));
    assertEquals("Asia/Tokyo", dataService.getTimezone(TsidUtils.toLong(post.getString("id"))));
  }
}
