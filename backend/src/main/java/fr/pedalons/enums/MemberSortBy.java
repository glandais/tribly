package fr.pedalons.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Sortable columns of a team's member list ({@code GET /api/teams/{teamSlug}/members}). The
 * repository always appends the membership id, so the order is total and pages never overlap.
 */
@RequiredArgsConstructor
@Getter
public enum MemberSortBy {
  /** When the member joined the team — the « nouveaux membres » of the administration panel. */
  JOINED_AT("joinedAt");

  final String field;
}
