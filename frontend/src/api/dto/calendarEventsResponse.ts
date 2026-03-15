import type { CalendarEventDto } from './calendarEventDto'

/**
 * Calendar events response
 */
export interface CalendarEventsResponse {
  /** List of calendar events */
  events: CalendarEventDto[]
}
