package fr.pedalons.enums;

import java.util.Locale;
import org.jspecify.annotations.Nullable;

/**
 * How much of each row a list response carries.
 *
 * <p>A feed row renders a title, a picture and two lines of text, yet the default response ships
 * every publication's full markdown body and its whole asset inventory — twenty articles for twenty
 * two-line summaries. {@link #COMPACT} lets a client say it will not render them.
 *
 * <p>The distinction is deliberately <b>not</b> a different schema: the same DTO comes back either
 * way, with {@code media.markdown} empty and {@code media.assets} trimmed rather than absent. Making
 * those two fields optional in the contract would have made them nullable in every generated client,
 * including the ones that send {@code MediaDto} back in a request body, for no gain a client can
 * actually use — {@code excerpt} and {@code thumbnailUrl} are what a compact row reads.
 *
 * <p>Trimmed, not emptied: a compact row keeps the logo, the first image and the themed thumbnails,
 * because no list DTO hoists them. {@code thumbnailUrl} is the map preview on a ride or a trip, not
 * the header photo; {@code RouteDto} collapses its themed thumbnails into one light-else-dark URL,
 * so a card that themes its picture cannot rebuild the dark one; and no {@code logoUrl} exists
 * outside {@code TeamDetailDto}. An inventory emptied wholesale left a card unable to draw its own
 * logo, which made the mode unusable for the lists it was built for.
 */
public enum ListViewMode {

  /** Everything, exactly as before this parameter existed. The default. */
  FULL,

  /**
   * Row summaries only: {@code media.markdown} comes back empty and {@code media.assets} trimmed to
   * the logo, the first image and the themed thumbnails. The markdown body, the attachments, the GPX
   * and FIT files and every image past the first are dropped. Read {@code excerpt} and {@code
   * thumbnailUrl} instead.
   */
  COMPACT;

  /**
   * Accepts {@code compact} as readily as {@code COMPACT}.
   *
   * <p>JAX-RS picks this up in preference to {@code valueOf}, so the query parameter is
   * case-insensitive even though the contract advertises the upper-case names.
   */
  public static ListViewMode fromString(String value) {
    return valueOf(value.trim().toUpperCase(Locale.ROOT));
  }

  /** Null-tolerant test: an absent parameter means {@link #FULL}. */
  public static boolean isCompact(@Nullable ListViewMode view) {
    return view == COMPACT;
  }
}
