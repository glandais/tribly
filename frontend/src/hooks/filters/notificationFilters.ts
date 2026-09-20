import { z } from 'zod'
import { COMMON_ALIAS, pageField, sizeField } from './common'

export const NOTIFICATION_PAGE_SIZE = 20

export const notificationFiltersSchema = z.object({
  /** The one filter the inbox offers. In the URL so "unread only" survives back-navigation. */
  unreadOnly: z
    .union([z.literal('true'), z.literal('false')])
    .transform((value) => value === 'true')
    .default(false)
    .catch(false),
  page: pageField,
  size: sizeField(NOTIFICATION_PAGE_SIZE),
})

export type NotificationFilters = z.infer<typeof notificationFiltersSchema>

export const notificationFiltersAlias = {
  ...COMMON_ALIAS,
  unreadOnly: 'unread',
} as const

/** True when the list is narrowed — what tells an empty page apart from an empty inbox. */
export function isNotificationFiltered(filters: NotificationFilters): boolean {
  return filters.unreadOnly
}
