import * as zod from 'zod'

/**
 * Get calendar events for all teams the user belongs to
 * @summary Get calendar events
 */
export const GetEventsQueryParams = zod.object({
  from: zod.string().optional().describe('Start date (ISO 8601)'),
  to: zod.string().optional().describe('End date (ISO 8601)'),
})

export const GetEventsResponse = zod
  .object({
    events: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Event ID (TSID)'),
            title: zod.string().describe('Event title'),
            start: zod.iso.datetime({ offset: true }).describe('Event start date\/time'),
            end: zod.iso.datetime({ offset: true }).optional().describe('Event end date\/time'),
            allDay: zod.boolean().describe('Is all-day event'),
            type: zod.enum(['RIDE', 'TRIP_STAGE']).describe('Event type'),
            teamSlug: zod.string().describe('Team slug'),
            teamName: zod.string().describe('Team name'),
            entitySlug: zod.string().describe('Entity slug (ride or stage)'),
            tripSlug: zod.string().optional().describe('Parent trip slug (for stages only)'),
            startPlaceName: zod
              .string()
              .optional()
              .describe(
                'Name of the meeting place, null when the ride or stage has no start place'
              ),
            distance: zod
              .number()
              .optional()
              .describe('Distance in meters of the attached route, null when there is no route'),
            elevationGain: zod
              .number()
              .optional()
              .describe(
                'Total elevation gain in meters of the attached route, null when there is no route'
              ),
            thumbnailUrl: zod
              .string()
              .optional()
              .describe(
                "Thumbnail image URL template (contains a {size} placeholder). Falls back to the route's thumbnail when the ride or stage has none of its own."
              ),
            registered: zod
              .boolean()
              .describe(
                'Whether the current user is registered to this ride, or to the trip this stage belongs to. False for an anonymous caller.'
              ),
            groupName: zod
              .string()
              .optional()
              .describe(
                'Name of the ride group the current user joined. Null when not registered, and always null for trip stages, which have no groups.'
              ),
            status: zod
              .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
              .describe('Publication status of the ride or stage'),
          })
          .describe('Calendar event data')
      )
      .describe('List of calendar events'),
  })
  .describe('Calendar events response')

/**
 * Get ICS calendar feed for all user's teams (requires token)
 * @summary Get global ICS feed
 */
export const GetGlobalIcsFeedQueryParams = zod.object({
  token: zod.string().optional().describe('Calendar access token'),
})

export const GetGlobalIcsFeedResponse = zod.unknown()

/**
 * Get or create the user's calendar token for ICS feed access
 * @summary Get calendar token
 */
export const GetTokenResponse = zod
  .object({
    token: zod.string().describe('ICS feed token (include in URL)'),
    globalFeedUrl: zod.string().describe('Global ICS feed URL'),
    teamFeedUrlTemplate: zod.string().describe('Team-specific feed URL template'),
  })
  .describe('Calendar token information')

/**
 * Regenerate the user's calendar token, invalidating the old one
 * @summary Regenerate calendar token
 */
export const RegenerateTokenResponse = zod
  .object({
    token: zod.string().describe('ICS feed token (include in URL)'),
    globalFeedUrl: zod.string().describe('Global ICS feed URL'),
    teamFeedUrlTemplate: zod.string().describe('Team-specific feed URL template'),
  })
  .describe('Calendar token information')

/**
 * Get calendar events for a specific team
 * @summary Get team calendar events
 */
export const GetTeamEventsParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const GetTeamEventsQueryParams = zod.object({
  from: zod.string().optional().describe('Start date (ISO 8601)'),
  to: zod.string().optional().describe('End date (ISO 8601)'),
})

export const GetTeamEventsResponse = zod
  .object({
    events: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Event ID (TSID)'),
            title: zod.string().describe('Event title'),
            start: zod.iso.datetime({ offset: true }).describe('Event start date\/time'),
            end: zod.iso.datetime({ offset: true }).optional().describe('Event end date\/time'),
            allDay: zod.boolean().describe('Is all-day event'),
            type: zod.enum(['RIDE', 'TRIP_STAGE']).describe('Event type'),
            teamSlug: zod.string().describe('Team slug'),
            teamName: zod.string().describe('Team name'),
            entitySlug: zod.string().describe('Entity slug (ride or stage)'),
            tripSlug: zod.string().optional().describe('Parent trip slug (for stages only)'),
            startPlaceName: zod
              .string()
              .optional()
              .describe(
                'Name of the meeting place, null when the ride or stage has no start place'
              ),
            distance: zod
              .number()
              .optional()
              .describe('Distance in meters of the attached route, null when there is no route'),
            elevationGain: zod
              .number()
              .optional()
              .describe(
                'Total elevation gain in meters of the attached route, null when there is no route'
              ),
            thumbnailUrl: zod
              .string()
              .optional()
              .describe(
                "Thumbnail image URL template (contains a {size} placeholder). Falls back to the route's thumbnail when the ride or stage has none of its own."
              ),
            registered: zod
              .boolean()
              .describe(
                'Whether the current user is registered to this ride, or to the trip this stage belongs to. False for an anonymous caller.'
              ),
            groupName: zod
              .string()
              .optional()
              .describe(
                'Name of the ride group the current user joined. Null when not registered, and always null for trip stages, which have no groups.'
              ),
            status: zod
              .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
              .describe('Publication status of the ride or stage'),
          })
          .describe('Calendar event data')
      )
      .describe('List of calendar events'),
  })
  .describe('Calendar events response')

/**
 * Get ICS calendar feed for a specific team (requires token)
 * @summary Get team ICS feed
 */
export const GetTeamIcsFeedParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const GetTeamIcsFeedQueryParams = zod.object({
  token: zod.string().optional().describe('Calendar access token'),
})

export const GetTeamIcsFeedResponse = zod.unknown()
