package fr.pedalons.service.migration.live;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.service.common.SlugService;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/** Biketeam ids become Pédalons slugs (decision §13 of the plan, 2026-09-24). */
class BiketeamSlugTest {

  private static final Pattern PEDALONS_SLUG = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");

  @Test
  void aPedalonsSlugAlready_isKept() {
    assertEquals("n-peloton", BiketeamTargetResolver.targetSlug("n-peloton"));
  }

  @Test
  void separatorRuns_becomeOneHyphen_andEdgeHyphensGo() {
    assertEquals("club-x", BiketeamTargetResolver.targetSlug("club_x"));
    assertEquals("club-x", BiketeamTargetResolver.targetSlug("club.x"));
    assertEquals("a-b-c", BiketeamTargetResolver.targetSlug("a._-b__c"));
    assertEquals("team", BiketeamTargetResolver.targetSlug("team_."));
    assertEquals("0-9", BiketeamTargetResolver.targetSlug("0--9-"));
  }

  @Test
  void aLongId_isCutAtTheSlugLimit_withoutATrailingHyphen() {
    // 255 characters, the longest biketeam id; the cut at 200 falls right after a separator.
    String id = "a".repeat(199) + "_" + "b".repeat(55);
    String slug = BiketeamTargetResolver.targetSlug(id);
    assertEquals("a".repeat(199), slug);
    assertTrue(PEDALONS_SLUG.matcher(slug).matches());
    assertEquals(200, BiketeamTargetResolver.targetSlug("c".repeat(255)).length());
  }

  @Test
  void aSetAsideSlug_keepsItsSuffix_withinTheLimit() {
    assertEquals(
        "n-peloton-reset-0hx3k2m9q8r4t",
        BiketeamLiveMigrationWorker.setAsideSlug("n-peloton", "0hx3k2m9q8r4t"));
    String longSlug = "x".repeat(179) + "-" + "y".repeat(20);
    String setAside = BiketeamLiveMigrationWorker.setAsideSlug(longSlug, "0hx3k2m9q8r4t");
    assertTrue(setAside.length() <= SlugService.MAX_SLUG_LENGTH, setAside);
    assertTrue(setAside.endsWith("-reset-0hx3k2m9q8r4t"));
    assertTrue(PEDALONS_SLUG.matcher(setAside).matches(), setAside);
  }
}
