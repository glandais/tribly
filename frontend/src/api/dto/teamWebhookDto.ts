import type { Instant } from './instant.ts'
import type { NotificationDeliveryStatus } from './notificationDeliveryStatus.ts'
import type { TeamWebhookKind } from './teamWebhookKind.ts'

/**
 * The team's outgoing webhook
 */
export interface TeamWebhookDto {
  /** Whether the team has a webhook at all */
  configured: boolean
  /** The URL, masked: scheme, host and the last characters only */
  maskedUrl?: string
  /** Message format, read from the URL */
  kind?: TeamWebhookKind
  /** Language the messages are written in */
  language?: string
  /** Whether announcements are posted */
  enabled: boolean
  /** Outcome of the latest attempt */
  lastStatus?: NotificationDeliveryStatus
  /** Why the latest attempt failed, when it did */
  lastError?: string
  /** When the latest attempt was made */
  lastAttemptAt?: Instant
}
