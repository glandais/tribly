import type {
  NotificationDto,
  NotificationListResponse,
  NotificationPreferencesDto,
  UnreadCountDto,
} from '../../src/api/dto'
import { apiGet, type AuthResponse } from './api'
import { expect } from './fixtures'

/**
 * The notifications journey's reads (flow-notifications.e2e.ts): a user's inbox and preferences
 * through the API, and the wait for the dispatcher.
 *
 * Nothing is notified synchronously: a publication or a comment queues an event, and
 * `NotificationScheduler.tick` fans the pending ones out every 15 s. So a notification shows up
 * anywhere between a moment and a full cycle after its cause — plus a busy tick, when the whole
 * suite runs at once.
 */

/** Long enough for two dispatcher cycles (15 s each) and a slow tick. */
export const DISPATCH_TIMEOUT_MS = 45_000

/** GET /api/notifications/unread-count, as `who`. */
export const unreadCount = async (who: AuthResponse) =>
  (await apiGet<UnreadCountDto>(who, '/api/notifications/unread-count')).count

/** The first page (newest first) of `who`'s inbox — big enough for any single test's. */
export const listNotifications = (who: AuthResponse, unreadOnly = false) =>
  apiGet<NotificationListResponse>(who, '/api/notifications', { size: 100, unreadOnly })

/**
 * Waits for the dispatcher to put in `who`'s inbox a notification matching `predicate`, and
 * returns it.
 */
export async function waitForNotification(
  who: AuthResponse,
  predicate: (notification: NotificationDto) => boolean,
  description = 'the expected notification'
): Promise<NotificationDto> {
  let found: NotificationDto | undefined
  await expect
    .poll(
      async () => {
        found = (await listNotifications(who)).items.find(predicate)
        return !!found
      },
      {
        message: `${description} reaches the inbox`,
        timeout: DISPATCH_TIMEOUT_MS,
        intervals: [1_000, 2_000],
      }
    )
    .toBe(true)
  return found!
}

/** The notification about `subjectSlug` of type `type` — each test's subjects are its own. */
export const about =
  (type: NotificationDto['type'], subjectSlug: string) => (notification: NotificationDto) =>
    notification.type === type && notification.subjectSlug === subjectSlug

/** GET /api/notifications/preferences, as `who`: the channels, the matrix and the team mutes. */
export const notificationPreferences = (who: AuthResponse) =>
  apiGet<NotificationPreferencesDto>(who, '/api/notifications/preferences')

/** Whether `who` muted the team, per the API; undefined when the team isn't among theirs. */
export const isMuted = async (who: AuthResponse, teamSlug: string) =>
  (await notificationPreferences(who)).teams.find((team) => team.teamSlug === teamSlug)?.muted
