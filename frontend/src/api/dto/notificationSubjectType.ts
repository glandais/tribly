export type NotificationSubjectType =
  (typeof NotificationSubjectType)[keyof typeof NotificationSubjectType]

export const NotificationSubjectType = {
  RIDE: 'RIDE',
  TRIP: 'TRIP',
  POST: 'POST',
  ROUTE: 'ROUTE',
} as const
