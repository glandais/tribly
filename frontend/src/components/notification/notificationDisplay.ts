import {
  IconBike,
  IconCalendarCancel,
  IconMessageReply,
  IconNews,
  IconRoute,
} from '@tabler/icons-react'
import { NotificationSubjectType, NotificationType } from '@/api/dto'
import type { NotificationDto } from '@/api/dto'
import { paths } from '@/config/paths'

/**
 * How a notification looks and where it leads.
 *
 * The API deliberately carries **no rendered text** (see
 * `docs/plans/2026-09-18-notifications.md` §3): a notification is a type plus structured fields,
 * and the client words it in the reader's own language. So this module — not the server — owns the
 * wording key, the icon and the destination, exactly as `ErrorCode` is worded client-side.
 */

/** What `@tabler/icons-react` actually exports — a forwardRef component, not a plain function. */
type TablerIcon = typeof IconBike

/** Icon per type. Cancellations read as cancellations at a glance, whatever the subject. */
const TYPE_ICONS: Record<NotificationType, TablerIcon> = {
  [NotificationType.RIDE_PUBLISHED]: IconBike,
  [NotificationType.RIDE_CANCELLED]: IconCalendarCancel,
  [NotificationType.TRIP_PUBLISHED]: IconRoute,
  [NotificationType.TRIP_CANCELLED]: IconCalendarCancel,
  [NotificationType.POST_PUBLISHED]: IconNews,
  [NotificationType.COMMENT_REPLY]: IconMessageReply,
}

/** Mantine palette *name*, never a hex — the theme resolves it per colour scheme. */
const TYPE_COLORS: Record<NotificationType, string> = {
  [NotificationType.RIDE_PUBLISHED]: 'primary',
  [NotificationType.RIDE_CANCELLED]: 'danger',
  [NotificationType.TRIP_PUBLISHED]: 'primary',
  [NotificationType.TRIP_CANCELLED]: 'danger',
  [NotificationType.POST_PUBLISHED]: 'primary',
  [NotificationType.COMMENT_REPLY]: 'primary',
}

export function notificationIcon(type: NotificationType): TablerIcon {
  return TYPE_ICONS[type]
}

export function notificationColor(type: NotificationType): string {
  return TYPE_COLORS[type]
}

/**
 * The page a notification opens, from its `subjectType` — never from its `type`, so a new type on
 * an existing subject needs nothing here.
 */
export function notificationPath(notification: NotificationDto): string {
  const { teamSlug, subjectSlug, subjectType } = notification
  switch (subjectType) {
    case NotificationSubjectType.RIDE:
      return paths.ride(teamSlug, subjectSlug)
    case NotificationSubjectType.TRIP:
      return paths.trip(teamSlug, subjectSlug)
    case NotificationSubjectType.POST:
      return paths.post(teamSlug, subjectSlug)
    case NotificationSubjectType.ROUTE:
      return paths.route(teamSlug, subjectSlug)
  }
}
