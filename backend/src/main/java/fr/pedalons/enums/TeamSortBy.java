package fr.pedalons.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Sortable columns of the team directory ({@code GET /api/teams}). Mirrors {@link RouteSortBy}; the
 * repository always appends the team id so the order is total and pages never overlap.
 */
@RequiredArgsConstructor
@Getter
public enum TeamSortBy {
  NAME("t.name"),
  /**
   * The member count the rows carry ({@code TeamDetailDto.memberCount}): the same subquery, so the
   * order matches the numbers shown.
   */
  MEMBER_COUNT("(SELECT COUNT(ut4) FROM UserTeam ut4 WHERE ut4.team.id = t.id)");

  final String field;
}
