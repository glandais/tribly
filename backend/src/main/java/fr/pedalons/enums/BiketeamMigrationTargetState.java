package fr.pedalons.enums;

/**
 * What a biketeam migration would land on, in the domain it is confirmed on. See
 * docs/plans/2026-09-22-biketeam-live-migration.md §4.1 and §7.3.
 */
public enum BiketeamMigrationTargetState {
  /**
   * No team at that slug, trash included — or a trashed team that came from this biketeam team,
   * which the migration will set aside and recreate.
   */
  NEW,
  /** An active team that came from this biketeam team: the migration updates it in place. */
  EXISTING_MIGRATED,
  /** The slug belongs to a team that did not come from this biketeam team. Never touched. */
  SLUG_CONFLICT,
  /** This biketeam team was already migrated to another domain of the same platform. */
  MIGRATED_IN_OTHER_DOMAIN
}
