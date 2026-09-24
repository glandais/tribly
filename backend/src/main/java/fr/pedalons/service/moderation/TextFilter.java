package fr.pedalons.service.moderation;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * The publication filter: refuses a text containing one of a short list of unambiguous terms —
 * heavy insults, slurs, hate phrases, in French and English (App Store guideline 1.2).
 *
 * <p>Deliberately narrow. Reporting covers everything else, and a false positive blocks an honest
 * member with no recourse, so a word that is also an ordinary word, or an insult only in some
 * contexts, is not on the list.
 *
 * <p>Text and terms go through the same {@link #normalize}: lower case, accents removed, simple
 * leetspeak undone, every non-letter turned into a space. A term matches a run of <em>whole</em>
 * words, so a term never matches inside a longer word.
 */
@ApplicationScoped
public class TextFilter {

  static final String TERMS_RESOURCE = "moderation/blocked-terms.txt";

  private static final Pattern MARKS = Pattern.compile("\\p{M}+");
  private static final Pattern NON_LETTERS = Pattern.compile("[^\\p{L}]+");

  /** Each term normalized, and padded with a space on both sides for whole-word matching. */
  private final List<String> paddedTerms;

  public TextFilter() {
    this(loadTerms(TERMS_RESOURCE));
  }

  TextFilter(Collection<String> terms) {
    List<String> padded = new ArrayList<>();
    for (String term : terms) {
      String normalized = normalize(term);
      if (!normalized.isEmpty()) {
        padded.add(" " + normalized + " ");
      }
    }
    this.paddedTerms = List.copyOf(padded);
  }

  /** Whether {@code text} may be published. A null or blank text always may. */
  public boolean isAcceptable(@Nullable String text) {
    if (text == null || text.isBlank()) {
      return true;
    }
    String padded = " " + normalize(text) + " ";
    for (String term : paddedTerms) {
      if (padded.contains(term)) {
        return false;
      }
    }
    return true;
  }

  /** How many terms are loaded — a list that failed to load silently would accept everything. */
  public int size() {
    return paddedTerms.size();
  }

  /**
   * Lower case, accents removed, leetspeak undone ({@code 0→o 1→i 3→e 4→a @→a $→s 5→s 7→t}), then
   * every run of non-letters collapsed to a single space, trimmed.
   */
  static String normalize(String text) {
    String lower = text.toLowerCase(Locale.ROOT);
    String unaccented =
        MARKS.matcher(Normalizer.normalize(lower, Normalizer.Form.NFD)).replaceAll("");
    StringBuilder unleet = new StringBuilder(unaccented.length());
    for (int i = 0; i < unaccented.length(); i++) {
      char c = unaccented.charAt(i);
      unleet.append(
          switch (c) {
            case '0' -> 'o';
            case '1' -> 'i';
            case '3' -> 'e';
            case '4', '@' -> 'a';
            case '5', '$' -> 's';
            case '7' -> 't';
            default -> c;
          });
    }
    return NON_LETTERS.matcher(unleet).replaceAll(" ").strip();
  }

  /** One term or phrase per line; blank lines and lines starting with {@code #} are skipped. */
  static List<String> loadTerms(String resource) {
    List<String> terms = new ArrayList<>();
    try (InputStream in =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
      if (in == null) {
        throw new IllegalStateException("Missing " + resource);
      }
      BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
      String line;
      while ((line = reader.readLine()) != null) {
        String term = line.strip();
        if (!term.isEmpty() && !term.startsWith("#")) {
          terms.add(term);
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return terms;
  }
}
