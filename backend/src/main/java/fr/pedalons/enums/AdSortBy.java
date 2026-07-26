package fr.pedalons.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Sortable columns of the classifieds listing. Mirrors {@link RouteSortBy}. */
@RequiredArgsConstructor
@Getter
public enum AdSortBy {
  /** Publication date — the default ordering, and what the listing did before sorting existed. */
  DATE_TIME("te.dateTime"),
  /** Asking price. Ads with no price sort last in either direction (SQL NULLs). */
  PRICE("te.price"),
  NAME("te.name");

  final String field;
}
