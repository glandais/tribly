package fr.pedalons.enums;

/**
 * Which side of now a dated publication — a ride or a trip — falls on, by its end rather than its
 * start (docs/LEDGER_*.md API-85): an outing that left ten minutes ago, or a trip that began
 * yesterday, is still {@link #UPCOMING}. A post has no end and is never in either.
 */
public enum PublicationWhen {

  /** Not over yet: {@code end >= now}, the ones under way included. Soonest departure first. */
  UPCOMING,

  /** Over: {@code end < now}. Latest departure first. */
  PAST
}
