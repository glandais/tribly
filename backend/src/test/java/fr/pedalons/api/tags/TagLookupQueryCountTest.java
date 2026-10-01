package fr.pedalons.api.tags;

import fr.pedalons.api.AbstractQueryCountTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ad.Ad;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ridetemplate.RideTemplate;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.tag.Tag;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.TagTarget;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tags on list pages resolve per page through {@code TagLookup}, never per row (docs/LEDGER_*.md
 * API-59): every seeded row carries two tags and a third of them a third tag, so a per-row lookup —
 * or walking the links of each row — would show in the statement or hydration count. See {@link
 * AbstractQueryCountTest}.
 */
@QuarkusTest
class TagLookupQueryCountTest extends AbstractQueryCountTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  private Tag[] tags(TagTarget type) {
    return new Tag[] {
      dataService.createTag(team1, user1, type, type + " one"),
      dataService.createTag(team1, user1, type, type + " two"),
      dataService.createTag(team1, user1, type, type + " three")
    };
  }

  private static Tag[] forRow(Tag[] tags, int i) {
    return i % 3 == 0 ? tags : new Tag[] {tags[0], tags[1]};
  }

  @Test
  void teamRoutes_withTags_costDoesNotScaleWithRowCount() {
    Tag[] tags = tags(TagTarget.ROUTE);
    for (int i = 0; i < LARGE_PAGE; i++) {
      Route route = dataService.createRoute(team1, user1, "Tagged Route " + i);
      dataService.tagContent(route, forRow(tags, i));
    }
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/routes (tagged)",
        asUser1(),
        "/api/teams/" + team1Slug + "/routes");
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/routes?tags= (tagged)",
        anonymous(),
        "/api/teams/" + team1Slug + "/routes?tags=" + TsidUtils.toString(tags[0].getId()));
    assertFlatQueryCount("GET /api/routes (tagged)", anonymous(), "/api/routes");
  }

  @Test
  void teamPosts_withTags_costDoesNotScaleWithRowCount() {
    Tag[] tags = tags(TagTarget.POST);
    Instant base = Instant.now().minus(1, ChronoUnit.DAYS);
    for (int i = 0; i < LARGE_PAGE; i++) {
      Post post = dataService.createPost(team1, user1, "Tagged Post " + i, base.plusSeconds(i));
      dataService.tagContent(post, forRow(tags, i));
    }
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/publications?type=POST (tagged)",
        asUser1(),
        "/api/teams/" + team1Slug + "/publications?type=POST");
    assertFlatQueryCount(
        "GET /api/publications (tagged)", anonymous(), "/api/publications?type=POST");
  }

  @Test
  void teamAds_withTags_costDoesNotScaleWithRowCount() {
    Tag[] tags = tags(TagTarget.AD);
    for (int i = 0; i < LARGE_PAGE; i++) {
      Ad ad = dataService.createAd(team1, user1, "Tagged Ad " + i, AdType.SALE);
      dataService.tagContent(ad, forRow(tags, i));
    }
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/classifieds (tagged)",
        asUser1(),
        "/api/teams/" + team1Slug + "/classifieds");
  }

  @Test
  void rideTemplates_withTags_costDoesNotScaleWithRowCount() {
    Tag[] tags = tags(TagTarget.RIDE);
    for (int i = 0; i < LARGE_PAGE; i++) {
      RideTemplate template =
          dataService.createRideTemplate(team1, user1, "Template " + i, "template-" + i);
      dataService.tagTemplate(template, forRow(tags, i));
    }
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/ride-templates (tagged)",
        asUser1(),
        "/api/teams/" + team1Slug + "/ride-templates");
  }
}
