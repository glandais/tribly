import type { NotificationDto } from './notificationDto.ts'

/**
 * A page of the current user's notifications, newest first
 */
export interface NotificationListResponse {
  /** The notifications of this page */
  items: NotificationDto[]
  /** How many notifications match the query (unread only, if asked) */
  total: number
  /** How many notifications are unread, whatever the filter */
  unreadCount: number
  /** Page number (0-indexed) */
  page: number
  /** Page size applied */
  size: number
}
