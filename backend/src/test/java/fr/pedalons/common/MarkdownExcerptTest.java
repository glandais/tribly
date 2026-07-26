package fr.pedalons.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * The excerpt is what a list row renders instead of the body, so anything markdown leaves behind
 * shows up as literal punctuation on a card. These tests are mostly about what does <em>not</em>
 * come out.
 */
class MarkdownExcerptTest {

  @Nested
  class Flattening {

    @Test
    void inlineLinkKeepsOnlyItsLabel() {
      assertEquals(
          "Voir le parcours du dimanche ici",
          MarkdownExcerpt.of("Voir le [parcours du dimanche](https://example.com/x?a=1) ici"));
    }

    @Test
    void imageKeepsOnlyItsAltText() {
      assertEquals(
          "Le col au sommet",
          MarkdownExcerpt.of("![Le col](https://example.com/col.jpg) au sommet"));
    }

    @Test
    void referenceLinkAndItsDefinitionBothGo() {
      assertEquals(
          "Le col est dur.",
          MarkdownExcerpt.of("Le [col][ref] est dur.\n\n[ref]: https://example.com/col"));
    }

    @Test
    void autolinkKeepsTheUrl() {
      assertEquals(
          "Inscription https://example.com/go ouverte",
          MarkdownExcerpt.of("Inscription <https://example.com/go> ouverte"));
    }

    @Test
    void headingsBulletsAndQuotesLoseTheirMarkers() {
      String markdown =
          """
          # Sortie du dimanche

          > Attention au vent.

          - premier point
          - second point
          """;
      assertEquals(
          "Sortie du dimanche Attention au vent. premier point second point",
          MarkdownExcerpt.of(markdown));
    }

    @Test
    void emphasisAndCodeLoseTheirDelimiters() {
      assertEquals(
          "Fort vent, vraiment fort, git pull avant",
          MarkdownExcerpt.of("**Fort** vent, *vraiment* fort, `git pull` avant"));
    }

    @Test
    void snakeCaseSurvivesTheEmphasisStripping() {
      assertEquals(
          "le fichier trace_du_jour est joint",
          MarkdownExcerpt.of("le fichier trace_du_jour est joint"));
    }

    @Test
    void fencedCodeBlockIsDropped() {
      assertEquals(
          "Avant Apres", MarkdownExcerpt.of("Avant\n\n```\nnot rendered at all\n```\n\nApres"));
    }

    @Test
    void tableBecomesWords() {
      String markdown =
          """
          | Groupe | Vitesse |
          | ------ | ------- |
          | A | 30 |
          """;
      assertEquals("Groupe Vitesse A 30", MarkdownExcerpt.of(markdown));
    }

    @Test
    void htmlIsDropped() {
      assertEquals(
          "Rendez-vous a 9h", MarkdownExcerpt.of("<div class=\"x\">Rendez-vous</div> a 9h"));
    }

    @Test
    void noResidualMarkdownSyntaxIsLeftBehind() {
      String markdown =
          """
          ## Titre

          **Gras**, _italique_, [lien](https://example.com), ![img](https://example.com/i.png)

          ---

          1. un
          2. deux
          """;
      String excerpt = MarkdownExcerpt.of(markdown);
      assertNotNull(excerpt);
      for (String syntax : new String[] {"#", "*", "[", "]", "(", ")", "_", "---"}) {
        assertFalse(excerpt.contains(syntax), "excerpt still holds '" + syntax + "': " + excerpt);
      }
    }
  }

  @Nested
  class Truncation {

    @Test
    void shortTextComesBackWhole() {
      assertEquals("Court.", MarkdownExcerpt.of("Court."));
    }

    @Test
    void longTextIsCutOnAWordBoundaryAndEllipsised() {
      String excerpt = MarkdownExcerpt.of("mot ".repeat(200));
      assertNotNull(excerpt);
      assertTrue(excerpt.endsWith("…"), excerpt);
      assertTrue(excerpt.length() <= MarkdownExcerpt.DEFAULT_MAX_LENGTH + 1, excerpt);
      // Cut between words, never through one.
      assertTrue(excerpt.substring(0, excerpt.length() - 1).endsWith("mot"), excerpt);
    }

    @Test
    void aSingleUnbreakableRunIsCutHard() {
      String excerpt = MarkdownExcerpt.of("x".repeat(400));
      assertNotNull(excerpt);
      assertEquals(MarkdownExcerpt.DEFAULT_MAX_LENGTH + 1, excerpt.length());
      assertTrue(excerpt.endsWith("…"));
    }

    @Test
    void aVeryLongBodyIsReadFromItsHeadAndStillCutOnAWord() {
      String excerpt = MarkdownExcerpt.of("Le peloton part a neuf heures. ".repeat(2000));
      assertNotNull(excerpt);
      assertTrue(excerpt.startsWith("Le peloton part a neuf heures."), excerpt);
      assertTrue(excerpt.length() <= MarkdownExcerpt.DEFAULT_MAX_LENGTH + 1, excerpt);
      assertTrue(excerpt.endsWith("…"), excerpt);
    }

    @Test
    void aVeryLongCodeBlockBeforeTheProseIsStillStrippedWhole() {
      // Longer than the bounded head: only the closing fence says where the block ends.
      String markdown = "```\n" + "x".repeat(9000) + "\n```\n\nEnfin du texte.";
      assertEquals("Enfin du texte.", MarkdownExcerpt.of(markdown));
    }

    @Test
    void theLengthIsConfigurable() {
      assertEquals("un deux…", MarkdownExcerpt.of("un deux trois quatre cinq six sept huit", 10));
    }
  }

  @Nested
  class Emptiness {

    @Test
    void nullMarkdownYieldsNull() {
      assertNull(MarkdownExcerpt.of(null));
    }

    @Test
    void blankMarkdownYieldsNull() {
      assertNull(MarkdownExcerpt.of("   \n\n  "));
    }

    @Test
    void markdownThatIsOnlySyntaxYieldsNullRatherThanAnEmptyLine() {
      assertNull(MarkdownExcerpt.of("---\n\n```\ncode\n```\n"));
    }
  }
}
