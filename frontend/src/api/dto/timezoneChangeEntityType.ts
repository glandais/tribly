export type TimezoneChangeEntityType =
  (typeof TimezoneChangeEntityType)[keyof typeof TimezoneChangeEntityType]

export const TimezoneChangeEntityType = {
  RIDE: 'RIDE',
  TRIP: 'TRIP',
  TRIP_STAGE: 'TRIP_STAGE',
  POST: 'POST',
} as const
