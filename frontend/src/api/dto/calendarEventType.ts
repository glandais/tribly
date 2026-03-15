export type CalendarEventType = (typeof CalendarEventType)[keyof typeof CalendarEventType]

export const CalendarEventType = {
  RIDE: 'RIDE',
  TRIP_STAGE: 'TRIP_STAGE',
} as const
