import type { Instant } from './instant.ts'
import type { NotificationChange } from './notificationChange.ts'
import type { NotificationSubjectType } from './notificationSubjectType.ts'
import type { NotificationType } from './notificationType.ts'

/**
 * A notification in the current user's inbox
 */
export interface NotificationDto {
  /** Notification identifier */
  id: string
  /** What happened */
  type: NotificationType
  /** Whether the user has read it */
  read: boolean
  /** When it was created */
  createdAt: Instant
  /** Display name of whoever caused it, when someone did (a scheduled publication has no actor) */
  actorName?: string
  /** Slug of the team it happened in */
  teamSlug: string
  /** Name of the team it happened in */
  teamName: string
  /** Kind of page the notification opens */
  subjectType: NotificationSubjectType
  /** Slug of the ride, trip, post or route — of the team, for TEAM */
  subjectSlug: string
  /** Name of the ride, trip, post or route — of the team, for TEAM */
  subjectName: string
  /** Date of the ride or trip, publication date of a post */
  subjectDateTime?: Instant
  /** A short quote: the comment, for COMMENT_REPLY and COMMENT_ON_MY_PUBLICATION; the name of the group joined, for RIDE_JOINED */
  excerpt?: string
  /** What changed, for RIDE_UPDATED; empty otherwise */
  changes: NotificationChange[]
}
