import type { Instant } from './instant.ts'
import type { TimezoneChangeEntityType } from './timezoneChangeEntityType.ts'

/**
 * An upcoming place-less event that keeps its wall time in the new zone
 */
export interface TeamTimezoneChangeItemDto {
  /** Kind of event */
  type: TimezoneChangeEntityType
  /** Event ID (TSID) */
  id: string
  /** Event URL slug */
  slug: string
  /** Event title; a stage's own name */
  title: string
  /** For a stage, the title of its trip */
  tripTitle?: string
  /** Start, as stored today: read it in the team's current zone for the wall time it keeps */
  dateTime: Instant
}
