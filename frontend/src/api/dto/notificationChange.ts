export type NotificationChange = (typeof NotificationChange)[keyof typeof NotificationChange]

export const NotificationChange = {
  DATE_TIME: 'DATE_TIME',
  START_PLACE: 'START_PLACE',
} as const
