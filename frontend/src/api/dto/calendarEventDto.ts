import type { CalendarEventType } from './calendarEventType.ts'
import type { Instant } from './instant.ts'
import type { Status } from './status.ts'

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
  /** Name of the meeting place, null when the ride or stage has no start place */
  startPlaceName?: string
  /** Distance in meters of the attached route, null when there is no route */
  distance?: number
  /** Total elevation gain in meters of the attached route, null when there is no route */
  elevationGain?: number
  /** Thumbnail image URL template (contains a {size} placeholder), light variant preferred. Falls back to the route's thumbnail when the ride or stage has none of its own. For a client that renders one picture and does not follow a colour scheme; prefer thumbnailLightUrl/thumbnailDarkUrl otherwise. */
  thumbnailUrl?: string
  /** Light-scheme thumbnail image URL template (contains a {size} placeholder). Null when the event's picture exists only in a dark variant. */
  thumbnailLightUrl?: string
  /** Dark-scheme thumbnail image URL template (contains a {size} placeholder). Null when the event's picture exists only in a light variant. */
  thumbnailDarkUrl?: string
  /** Whether the current user is registered to this ride, or to the trip this stage belongs to. False for an anonymous caller. */
  registered: boolean
  /** Name of the ride group the current user joined. Null when not registered, and always null for trip stages, which have no groups. */
  groupName?: string
  /** Publication status of the ride or stage */
  status: Status
}
