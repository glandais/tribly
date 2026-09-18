export type NotificationType = (typeof NotificationType)[keyof typeof NotificationType]

export const NotificationType = {
  RIDE_PUBLISHED: 'RIDE_PUBLISHED',
  RIDE_CANCELLED: 'RIDE_CANCELLED',
  TRIP_PUBLISHED: 'TRIP_PUBLISHED',
  TRIP_CANCELLED: 'TRIP_CANCELLED',
  POST_PUBLISHED: 'POST_PUBLISHED',
  COMMENT_REPLY: 'COMMENT_REPLY',
} as const
