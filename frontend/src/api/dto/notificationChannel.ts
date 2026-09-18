export type NotificationChannel = (typeof NotificationChannel)[keyof typeof NotificationChannel]

export const NotificationChannel = {
  IN_APP: 'IN_APP',
  EMAIL: 'EMAIL',
  PUSH: 'PUSH',
} as const
