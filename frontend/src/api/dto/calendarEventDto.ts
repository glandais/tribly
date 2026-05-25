import type { CalendarEventType } from './calendarEventType.ts'
import type { Instant } from './instant.ts'

/**
 * Calendar event data
 */
export interface CalendarEventDto {
  /** Event ID (TSID) */
  id: string
  /** Event title */
  title: string
  /** Event start date/time */
  start: Instant
  /** Event end date/time */
  end?: Instant
  /** Is all-day event */
  allDay: boolean
  /** Event type */
  type: CalendarEventType
  /** Team slug */
  teamSlug: string
  /** Team name */
  teamName: string
  /** Entity slug (ride or stage) */
  entitySlug: string
  /** Parent trip slug (for stages only) */
  tripSlug?: string
}
