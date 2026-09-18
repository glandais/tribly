import * as zod from 'zod'

/**
 * The current user's notifications, newest first. Each carries a type and structured fields, not rendered text: the client words it in its own language.
 * @summary List my notifications
 */
export const listMyNotificationsQueryPageDefault = 0
export const listMyNotificationsQuerySizeDefault = 20
export const listMyNotificationsQueryUnreadOnlyDefault = false

export const ListMyNotificationsQueryParams = zod.object({
  page: zod.int().default(listMyNotificationsQueryPageDefault).describe('Page number (0-indexed)'),
  size: zod.int().default(listMyNotificationsQuerySizeDefault).describe('Page size (max 200)'),
  unreadOnly: zod
    .boolean()
    .default(listMyNotificationsQueryUnreadOnlyDefault)
    .describe('Only unread notifications'),
})

export const ListMyNotificationsResponse = zod
  .object({
    items: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Notification identifier'),
            type: zod
              .enum([
                'RIDE_PUBLISHED',
                'RIDE_CANCELLED',
                'TRIP_PUBLISHED',
                'TRIP_CANCELLED',
                'POST_PUBLISHED',
                'COMMENT_REPLY',
              ])
              .describe('What happened'),
            read: zod.boolean().describe('Whether the user has read it'),
            createdAt: zod.iso.datetime({ offset: true }).describe('When it was created'),
            actorName: zod
              .string()
              .optional()
              .describe(
                'Display name of whoever caused it, when someone did (a scheduled publication has no actor)'
              ),
            teamSlug: zod.string().describe('Slug of the team it happened in'),
            teamName: zod.string().describe('Name of the team it happened in'),
            subjectType: zod
              .enum(['RIDE', 'TRIP', 'POST', 'ROUTE'])
              .describe('Kind of page the notification opens'),
            subjectSlug: zod.string().describe('Slug of the ride, trip, post or route'),
            subjectName: zod.string().describe('Name of the ride, trip, post or route'),
            subjectDateTime: zod.iso
              .datetime({ offset: true })
              .optional()
              .describe('Date of the ride or trip, publication date of a post'),
            excerpt: zod
              .string()
              .optional()
              .describe('A short quote — the reply, for COMMENT_REPLY'),
          })
          .describe("A notification in the current user's inbox")
      )
      .describe('The notifications of this page'),
    total: zod.int().describe('How many notifications match the query (unread only, if asked)'),
    unreadCount: zod.int().describe('How many notifications are unread, whatever the filter'),
    page: zod.int().describe('Page number (0-indexed)'),
    size: zod.int().describe('Page size applied'),
  })
  .describe("A page of the current user's notifications, newest first")

/**
 * A partial update: only the cells sent change. The full matrix is returned.
 * @summary Update my notification preferences
 */
export const updateMyNotificationPreferencesBodyPreferencesMax = 200

export const UpdateMyNotificationPreferencesBody = zod
  .object({
    preferences: zod
      .array(
        zod
          .object({
            type: zod
              .enum([
                'RIDE_PUBLISHED',
                'RIDE_CANCELLED',
                'TRIP_PUBLISHED',
                'TRIP_CANCELLED',
                'POST_PUBLISHED',
                'COMMENT_REPLY',
              ])
              .describe('Notification type'),
            channel: zod
              .enum(['IN_APP', 'EMAIL', 'PUSH'])
              .describe('Delivery channel. IN_APP is refused: the inbox is always on.'),
            enabled: zod.boolean().describe('Whether to deliver it'),
          })
          .describe('Switch one notification type on or off on one channel')
      )
      .max(updateMyNotificationPreferencesBodyPreferencesMax)
      .describe('The cells to change'),
  })
  .describe('Notification preference cells to change')

export const UpdateMyNotificationPreferencesResponse = zod
  .object({
    channels: zod
      .array(zod.enum(['IN_APP', 'EMAIL', 'PUSH']))
      .describe('Channels that can be configured on this server, in display order'),
    preferences: zod
      .array(
        zod
          .object({
            type: zod
              .enum([
                'RIDE_PUBLISHED',
                'RIDE_CANCELLED',
                'TRIP_PUBLISHED',
                'TRIP_CANCELLED',
                'POST_PUBLISHED',
                'COMMENT_REPLY',
              ])
              .describe('Notification type'),
            channel: zod.enum(['IN_APP', 'EMAIL', 'PUSH']).describe('Delivery channel'),
            enabled: zod.boolean().describe('Whether it is delivered, as currently in effect'),
            enabledByDefault: zod
              .boolean()
              .describe(
                "What applies when the user never touched this cell. Lets a client offer a 'restore defaults' without hard-coding them."
              ),
          })
          .describe('One cell of the notification preferences: a type on a channel')
      )
      .describe('One cell per type and configurable channel'),
  })
  .describe("The current user's notification preferences")

/**
 * Every notification type on every channel this server can deliver on. The inbox is not listed: it always receives everything.
 * @summary Get my notification preferences
 */
export const GetMyNotificationPreferencesResponse = zod
  .object({
    channels: zod
      .array(zod.enum(['IN_APP', 'EMAIL', 'PUSH']))
      .describe('Channels that can be configured on this server, in display order'),
    preferences: zod
      .array(
        zod
          .object({
            type: zod
              .enum([
                'RIDE_PUBLISHED',
                'RIDE_CANCELLED',
                'TRIP_PUBLISHED',
                'TRIP_CANCELLED',
                'POST_PUBLISHED',
                'COMMENT_REPLY',
              ])
              .describe('Notification type'),
            channel: zod.enum(['IN_APP', 'EMAIL', 'PUSH']).describe('Delivery channel'),
            enabled: zod.boolean().describe('Whether it is delivered, as currently in effect'),
            enabledByDefault: zod
              .boolean()
              .describe(
                "What applies when the user never touched this cell. Lets a client offer a 'restore defaults' without hard-coding them."
              ),
          })
          .describe('One cell of the notification preferences: a type on a channel')
      )
      .describe('One cell per type and configurable channel'),
  })
  .describe("The current user's notification preferences")

/**
 * @summary Mark all my notifications read
 */
export const MarkAllNotificationsReadResponse = zod.void()

/**
 * The badge on the bell. Cheap by design: clients poll it (on focus, at most once a minute) rather than reloading the list.
 * @summary Count my unread notifications
 */
export const CountMyUnreadNotificationsResponse = zod
  .object({
    count: zod.int().describe('Unread notifications'),
  })
  .describe('Number of unread notifications — the badge on the bell')

/**
 * Idempotent: marking an already-read notification read succeeds.
 * @summary Mark a notification read
 */
export const MarkNotificationReadParams = zod.object({
  notificationId: zod.string(),
})

export const MarkNotificationReadResponse = zod.void()
