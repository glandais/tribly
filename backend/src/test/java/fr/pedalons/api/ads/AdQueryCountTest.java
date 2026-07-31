package fr.pedalons.api.ads;

import fr.pedalons.api.AbstractQueryCountTest;
import fr.pedalons.enums.AdType;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Database-cost budget for the classifieds list and count endpoints.
 *
 * <p>See {@link AbstractQueryCountTest}. The count endpoint never reads a row to begin with, so its
 * cost is trivially flat — the test still guards against a future rewrite that would make it do so.
 */
@QuarkusTest
class AdQueryCountTest extends AbstractQueryCountTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  private void seedAds(int count) {
    for (int i = 0; i < count; i++) {
      dataService.createAd(team1, user1, "Budget Ad " + i, AdType.SALE);
    }
  }

  @Test
  void listTeamAds_costDoesNotScaleWithRowCount() {
    seedAds(LARGE_PAGE);
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/classifieds",
        asUser1(),
        "/api/teams/" + team1Slug + "/classifieds");
  }

  @Test
  void countTeamAds_costDoesNotScaleWithRowCount() {
    seedAds(LARGE_PAGE);
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/classifieds/count",
        asUser1(),
        "/api/teams/" + team1Slug + "/classifieds/count");
  }
}
