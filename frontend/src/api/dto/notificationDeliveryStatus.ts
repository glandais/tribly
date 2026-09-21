export type NotificationDeliveryStatus =
  (typeof NotificationDeliveryStatus)[keyof typeof NotificationDeliveryStatus]

export const NotificationDeliveryStatus = {
  PENDING: 'PENDING',
  SENDING: 'SENDING',
  SENT: 'SENT',
  SKIPPED: 'SKIPPED',
  FAILED: 'FAILED',
} as const
