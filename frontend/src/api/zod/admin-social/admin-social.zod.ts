import * as zod from 'zod'

/**
 * Create social-identity rows for migrated placeholder Strava accounts so they can log in with Strava. Idempotent.
 * @summary Backfill Strava identities
 */
export const BackfillStravaIdentitiesQueryParams = zod.object({
  domainId: zod.string().optional().describe("Target domain ID (defaults to the request's domain)"),
})

export const BackfillStravaIdentitiesResponse = zod
  .object({
    identitiesCreated: zod.int().describe('Number of identity rows created'),
  })
  .describe('Result of a social-identity backfill')
