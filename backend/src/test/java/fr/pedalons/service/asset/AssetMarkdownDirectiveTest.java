package fr.pedalons.service.asset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** docs/LEDGER_*.md SEC-10: the asset ids a markdown body references, read in linear time. */
class AssetMarkdownDirectiveTest {

  @Test
  void readsTheIdOfEachDirective() {
    assertEquals(
        Set.of("0abc", "0def"),
        AssetService.extractAssetIdsFromMarkdown(
            "Intro ::asset{id=\"0abc\"} middle\n\n"
                + "::asset{width=\"50%\" id=\"0def\" align=\"left\"}"));
  }

  @Test
  void ignoresAnUnclosedDirectiveOrOneWithoutId() {
    assertEquals(
        Set.of("0ok"),
        AssetService.extractAssetIdsFromMarkdown(
            "::asset{id=\"0lost\" ::asset{width=\"1\"} ::asset{id=\"0ok\"}"));
    assertEquals(Set.of(), AssetService.extractAssetIdsFromMarkdown(""));
  }

  @Test
  void keepsTheLastIdOfADirective_asTheFormerPatternDid() {
    assertEquals(
        Set.of("0second"),
        AssetService.extractAssetIdsFromMarkdown("::asset{id=\"0first\" id=\"0second\"}"));
  }

  @Test
  void aRunOfUnclosedDirectivesIsReadInLinearTime() {
    // 280 KB: the former pattern took 78 s on a tenth of it.
    String hostile = "::asset{id=\"a\"".repeat(20_000);
    assertTimeoutPreemptively(
        Duration.ofSeconds(2),
        () -> assertEquals(Set.of(), AssetService.extractAssetIdsFromMarkdown(hostile)));
  }
}
