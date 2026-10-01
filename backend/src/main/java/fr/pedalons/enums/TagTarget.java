package fr.pedalons.enums;

import fr.pedalons.domain.common.TeamEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The kind of content a team tag applies to: one vocabulary per kind (docs/LEDGER_*.md API-59,
 * plan D3). A {@code RIDE} tag never goes on a post. The first three repeat {@code
 * PublicationType}.
 */
@Getter
@RequiredArgsConstructor
public enum TagTarget {
  RIDE(TeamEntityType.RIDE),
  POST(TeamEntityType.POST),
  TRIP(TeamEntityType.TRIP),
  ROUTE(TeamEntityType.ROUTE),
  AD(TeamEntityType.AD);

  private final TeamEntityType teamEntityType;

  /** The vocabulary a content draws from. Team pages and trip stages carry no tag. */
  public static TagTarget of(TeamEntity entity) {
    TeamEntityType type = TeamEntityType.fromEntity(entity);
    for (TagTarget target : values()) {
      if (target.teamEntityType == type) {
        return target;
      }
    }
    throw new IllegalArgumentException("No tag vocabulary for " + type);
  }
}
