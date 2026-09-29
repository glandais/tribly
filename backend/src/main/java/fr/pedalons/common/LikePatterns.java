package fr.pedalons.common;

/**
 * {@code LIKE} patterns built from what a user typed.
 *
 * <p>The term is matched literally: {@code %} and {@code _} typed in a search box are characters to
 * find, not wildcards — unescaped, {@code %} matched every row and {@code _} any character
 * (docs/LEDGER_*.md SEC-22, audit L12). They are escaped with {@code !}, which every query using
 * such a pattern must declare with {@link #ESCAPE}: relying on PostgreSQL's default backslash does
 * not work, the SQL Hibernate generates does not leave it in force.
 */
public final class LikePatterns {

  /** The clause that goes after {@code LIKE :param} for a pattern from {@link #contains}. */
  public static final String ESCAPE = "escape '!'";

  private LikePatterns() {}

  /** A pattern matching any value that contains {@code term}, as typed. */
  public static String contains(String term) {
    return "%" + escape(term) + "%";
  }

  static String escape(String term) {
    return term.replace("!", "!!").replace("%", "!%").replace("_", "!_");
  }
}
