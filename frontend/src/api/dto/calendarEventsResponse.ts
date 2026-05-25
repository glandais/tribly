import type { CalendarEventDto } from './calendarEventDto.ts'

/**
 * Calendar events response
 */
export interface CalendarEventsResponse {
  /** List of calendar events */
  events: CalendarEventDto[]
}
