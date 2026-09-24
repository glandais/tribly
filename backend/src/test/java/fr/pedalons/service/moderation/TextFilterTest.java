package fr.pedalons.service.moderation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/** The publication filter, as a plain unit test: the real term list, then a list of its own. */
class TextFilterTest {

  private final TextFilter filter = new TextFilter();

  @Test
  void loadsTheTermList() {
    assertTrue(filter.size() > 50, "only " + filter.size() + " terms loaded");
  }

  @Test
  void acceptsOrdinaryText() {
    assertTrue(filter.isAcceptable("Belle sortie ce matin, 80 km et 1200 m de D+ !"));
    assertTrue(filter.isAcceptable("Sale: race wheels Zipp 404"));
    assertTrue(filter.isAcceptable("Selling my white power meter"));
    assertTrue(filter.isAcceptable("White Power2Max, like new"));
    assertTrue(filter.isAcceptable("Kike Martinez"));
    assertTrue(filter.isAcceptable(""));
    assertTrue(filter.isAcceptable("   "));
    assertTrue(filter.isAcceptable(null));
  }

  @Test
  void rejectsAListedTerm() {
    assertFalse(filter.isAcceptable("Quel connard ce chauffeur"));
    assertFalse(filter.isAcceptable("you motherfucker"));
  }

  @Test
  void ignoresCaseAndAccents() {
    assertFalse(filter.isAcceptable("ENCULÉ"));
    assertFalse(filter.isAcceptable("encule"));
    assertFalse(filter.isAcceptable("Enculee !"));
  }

  @Test
  void undoesSimpleLeetspeak() {
    assertFalse(filter.isAcceptable("s4l0pe"));
    assertFalse(filter.isAcceptable("$alope"));
    assertFalse(filter.isAcceptable("c0nn4rd"));
    assertFalse(filter.isAcceptable("5@lope"));
  }

  @Test
  void matchesAcrossPunctuation() {
    assertFalse(filter.isAcceptable("...connard!!!"));
    assertFalse(filter.isAcceptable("fils-de-pute"));
    assertFalse(filter.isAcceptable("fils_de_pute"));
  }

  @Test
  void matchesWholeWordsOnly() {
    // Words that merely contain a listed term.
    assertTrue(filter.isAcceptable("Mon cuissard salopette est trop grand"));
    assertTrue(filter.isAcceptable("Une dispute sur l'itinéraire, rien de grave"));
    assertTrue(filter.isAcceptable("Un coureur réputé"));
    assertTrue(filter.isAcceptable("Sortie à Scunthorpe"));
    assertTrue(filter.isAcceptable("computer"));
  }

  @Test
  void leavesAmbiguousFrenchWordsAlone() {
    assertTrue(filter.isAcceptable("Quel con ce vent de face"));
    assertTrue(filter.isAcceptable("Départ avec du retard"));
    assertTrue(filter.isAcceptable("Je crève de faim après la montée"));
    assertTrue(filter.isAcceptable("Putain, quelle montée"));
    assertTrue(filter.isAcceptable("Pain bâtard à la boulangerie du départ"));
    assertTrue(filter.isAcceptable("Frais de port offerts, fdp inclus"));
  }

  @Test
  void matchesAPhraseAsARunOfWords() {
    TextFilter phrases = new TextFilter(List.of("fils de pute", "", "Ôté"));

    assertEquals(2, phrases.size());
    assertFalse(phrases.isAcceptable("espèce de fils   de\npute"));
    assertTrue(phrases.isAcceptable("fils de putois"));
    assertTrue(phrases.isAcceptable("fils de"));
    assertFalse(phrases.isAcceptable("ote"), "terms are normalized like the text");
  }

  @Test
  void normalize_isLowerCaseUnaccentedLettersSeparatedBySingleSpaces() {
    assertEquals("ca va tres bien", TextFilter.normalize("  Ça   va — TRÈS bien !! "));
    assertEquals("salope", TextFilter.normalize("$4l0p3"));
    assertEquals("a b", TextFilter.normalize("a2b"));
  }
}
