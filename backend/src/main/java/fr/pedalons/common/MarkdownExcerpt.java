package fr.pedalons.common;

import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Turns a markdown body into a short plain-text excerpt fit for a list row.
 *
 * <p>A list row shows two lines of text, so shipping the whole article to produce them is the
 * expensive half of a feed response: twenty publications carry twenty full bodies for forty lines
 * that get rendered. The excerpt is computed here, once per row, and the body itself can then stay
 * home (see {@code ?view=compact}).
 *
 * <p>The output is <b>plain text</b>, not truncated markdown: a link becomes its label, an image
 * becomes its alt text, headings, emphasis, list bullets, blockquote markers, table pipes and fenced
 * code are dropped. Truncating markdown instead would leave a client rendering half a link or an
 * unclosed bold span, which is exactly the kind of thing that looks fine in the happy case and
 * breaks on the article that matters.
 *
 * <p>The target length is deliberately larger than any mock: the designs show 150 characters or two
 * lines depending on the screen, so the server hands over a little more and lets the client do the
 * final cut with the font metrics it alone knows.
 */
public final class MarkdownExcerpt {

  /** Roughly two lines on a phone, with margin for a client that shows three. */
  public static final int DEFAULT_MAX_LENGTH = 200;

  private static final Pattern FENCED_CODE = Pattern.compile("(?s)```.*?```|~~~.*?~~~");
  private static final Pattern HTML_COMMENT = Pattern.compile("(?s)<!--.*?-->");
  private static final Pattern LINK_DEFINITION = Pattern.compile("(?m)^ {0,3}\\[[^]]+]:.*$");
  private static final Pattern IMAGE = Pattern.compile("!\\[([^]]*)]\\([^)]*\\)");
  private static final Pattern INLINE_LINK = Pattern.compile("\\[([^]]*)]\\([^)]*\\)");
  private static final Pattern REFERENCE_LINK = Pattern.compile("\\[([^]]*)]\\[[^]]*]");
  private static final Pattern AUTOLINK = Pattern.compile("<((?:https?|mailto):[^>\\s]+)>");
  private static final Pattern HTML_TAG = Pattern.compile("(?s)</?[A-Za-z][^>]*>");
  private static final Pattern TABLE_DELIMITER = Pattern.compile("(?m)^ *\\|?[ :|-]*\\|[ :|-]*$");
  private static final Pattern THEMATIC_BREAK =
      Pattern.compile("(?m)^ {0,3}([-*_])(?: *\\1){2,} *$");
  private static final Pattern SETEXT_UNDERLINE = Pattern.compile("(?m)^ {0,3}(?:=+|-+) *$");
  private static final Pattern HEADING = Pattern.compile("(?m)^ {0,3}#{1,6} *");
  private static final Pattern BLOCKQUOTE = Pattern.compile("(?m)^ *(?:> ?)+");
  private static final Pattern LIST_MARKER = Pattern.compile("(?m)^ *(?:[-*+]|\\d+[.)]) +");
  private static final Pattern TASK_MARKER = Pattern.compile("(?m)^ *\\[[ xX]] +");
  private static final Pattern INLINE_CODE = Pattern.compile("`+([^`]*)`+");
  private static final Pattern STRONG_STAR = Pattern.compile("(?s)\\*\\*(.+?)\\*\\*");
  private static final Pattern STRONG_UNDERSCORE = Pattern.compile("(?s)__(.+?)__");
  private static final Pattern STRIKETHROUGH = Pattern.compile("(?s)~~(.+?)~~");
  private static final Pattern EMPHASIS_STAR = Pattern.compile("\\*(?=\\S)([^*\\n]+?)(?<=\\S)\\*");
  private static final Pattern EMPHASIS_UNDERSCORE =
      Pattern.compile("(?<![\\w_])_(?=\\S)([^_\\n]+?)(?<=\\S)_(?![\\w_])");
  private static final Pattern ESCAPED = Pattern.compile("\\\\([\\\\`*_{}\\[\\]()#+\\-.!|>~])");
  private static final Pattern WHITESPACE = Pattern.compile("\\s+");
  private static final Pattern TRAILING_PUNCTUATION = Pattern.compile("[\\p{Punct}\\s]+$");

  private MarkdownExcerpt() {}

  /** The excerpt of {@code markdown} at the default length, or {@code null} if it has no text. */
  public static @Nullable String of(@Nullable String markdown) {
    return of(markdown, DEFAULT_MAX_LENGTH);
  }

  /**
   * The excerpt of {@code markdown}, at most {@code maxLength} characters plus the ellipsis.
   *
   * @return {@code null} when the markdown is null, blank, or holds nothing but syntax (a lone
   *     image, a horizontal rule) — an empty string would make a client render an empty line where
   *     it could render nothing at all
   */
  public static @Nullable String of(@Nullable String markdown, int maxLength) {
    if (markdown == null || markdown.isBlank()) {
      return null;
    }
    // Fenced code is stripped from the whole body first: only the closing fence says where a block
    // ends, so a bounded head that landed inside one would come out as literal backticks.
    String source = FENCED_CODE.matcher(normalizeNewlines(markdown)).replaceAll(" ");
    // Everything else is then read from a bounded head. A page of twenty rows runs this twenty
    // times and an article can be tens of kilobytes for two rendered lines, so the cost — including
    // the backtracking of the emphasis patterns — stays proportional to what is returned.
    int scanLimit = scanLimit(maxLength);
    boolean bounded = source.length() > scanLimit;
    String flattened = flatten(bounded ? source.substring(0, scanLimit) : source);
    if (bounded && flattened.length() <= maxLength) {
      // The head was nearly all syntax, so it is not a faithful head: pay for the whole body rather
      // than return a cut that the bound, not the text, decided.
      flattened = flatten(source);
    }
    if (flattened.isEmpty()) {
      return null;
    }
    return truncate(flattened, maxLength);
  }

  /** Enough raw markdown to be sure of filling {@code maxLength} characters of prose. */
  private static int scanLimit(int maxLength) {
    return Math.max(1_000, maxLength * 20);
  }

  private static String normalizeNewlines(String markdown) {
    return markdown.replace("\r\n", "\n").replace('\r', '\n');
  }

  /** Markdown to plain text, whitespace collapsed. Package-visible so it can be tested alone. */
  static String flatten(String markdown) {
    String text = normalizeNewlines(markdown);
    text = FENCED_CODE.matcher(text).replaceAll(" ");
    text = HTML_COMMENT.matcher(text).replaceAll(" ");
    text = LINK_DEFINITION.matcher(text).replaceAll(" ");
    // Images before links: "![alt](url)" also matches the link pattern, minus the bang.
    text = IMAGE.matcher(text).replaceAll("$1");
    text = INLINE_LINK.matcher(text).replaceAll("$1");
    text = REFERENCE_LINK.matcher(text).replaceAll("$1");
    text = AUTOLINK.matcher(text).replaceAll("$1");
    text = HTML_TAG.matcher(text).replaceAll(" ");
    text = TABLE_DELIMITER.matcher(text).replaceAll(" ");
    text = THEMATIC_BREAK.matcher(text).replaceAll(" ");
    text = SETEXT_UNDERLINE.matcher(text).replaceAll(" ");
    text = HEADING.matcher(text).replaceAll("");
    text = BLOCKQUOTE.matcher(text).replaceAll("");
    text = LIST_MARKER.matcher(text).replaceAll("");
    text = TASK_MARKER.matcher(text).replaceAll("");
    text = INLINE_CODE.matcher(text).replaceAll("$1");
    text = STRONG_STAR.matcher(text).replaceAll("$1");
    text = STRONG_UNDERSCORE.matcher(text).replaceAll("$1");
    text = STRIKETHROUGH.matcher(text).replaceAll("$1");
    text = EMPHASIS_STAR.matcher(text).replaceAll("$1");
    text = EMPHASIS_UNDERSCORE.matcher(text).replaceAll("$1");
    // Table cells become words rather than one run-on token.
    text = text.replace('|', ' ');
    text = ESCAPED.matcher(text).replaceAll("$1");
    return WHITESPACE.matcher(text).replaceAll(" ").trim();
  }

  /**
   * Cuts on a word boundary, never mid-word, and marks the cut with an ellipsis.
   *
   * <p>When the first {@code maxLength} characters hold no space at all — a URL, a language that
   * does not space its words — the cut falls at {@code maxLength}: a hard cut beats returning a
   * whole paragraph.
   */
  private static String truncate(String text, int maxLength) {
    if (text.length() <= maxLength) {
      return text;
    }
    int cut = text.lastIndexOf(' ', maxLength);
    if (cut <= 0) {
      cut = maxLength;
    }
    String head = TRAILING_PUNCTUATION.matcher(text.substring(0, cut)).replaceAll("");
    if (head.isEmpty()) {
      head = text.substring(0, cut).trim();
    }
    return head + "…";
  }
}
