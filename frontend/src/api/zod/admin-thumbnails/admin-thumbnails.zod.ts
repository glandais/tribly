import * as zod from 'zod'

/**
 * Redraw the light and dark map thumbnails of routes, rides and trips from the geometry already stored, selected by drawing date, stored size or absence. Synchronous; run it with dryRun first.
 * @summary Regenerate map thumbnails
 */
export const AdminRegenerateThumbnailsQueryParams = zod.object({
  domainId: zod.string().optional().describe("Target domain ID (defaults to the request's domain)"),
})

export const adminRegenerateThumbnailsBodyLimitMax = 1000

export const AdminRegenerateThumbnailsBody = zod
  .object({
    renderedFrom: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe('Only thumbnails drawn at or after this instant'),
    renderedTo: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe('Only thumbnails drawn before this instant'),
    suspectBelowBytes: zod
      .int()
      .min(1)
      .optional()
      .describe(
        'Only thumbnails whose stored file is smaller than this many bytes, or has no file. A map drawn without its background weighs a few kB, a real one tens.'
      ),
    includeMissing: zod
      .boolean()
      .optional()
      .describe(
        'Also redraw the routes, rides and trips that have a route to draw but lack a light or dark thumbnail — what a failed render leaves behind'
      ),
    dryRun: zod.boolean().describe('List what would be redrawn, without redrawing anything'),
    limit: zod
      .int()
      .min(1)
      .max(adminRegenerateThumbnailsBodyLimitMax)
      .optional()
      .describe('Redraw at most this many entities (default 100)'),
  })
  .describe(
    "Which map thumbnails to redraw from the geometry already stored. The criteria combine: the date window and the size threshold narrow the thumbnails that exist, the 'missing' flag adds the entities that have none. At least one criterion is required."
  )

export const AdminRegenerateThumbnailsResponse = zod
  .object({
    dryRun: zod.boolean().describe('Whether this was a dry run (nothing redrawn)'),
    matched: zod.int().describe('Number of entities matching the criteria, before the limit'),
    entities: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Entity ID'),
            kind: zod.enum(['ROUTE', 'RIDE', 'TRIP']).describe('Entity kind'),
            teamSlug: zod.string().describe('Team slug'),
            slug: zod.string().describe('Entity slug'),
            reasons: zod
              .array(zod.enum(['RENDERED_IN_WINDOW', 'SMALL_FILE', 'MISSING']))
              .describe('Why it was selected'),
            before: zod
              .array(
                zod
                  .object({
                    type: zod.string().describe('Asset type'),
                    bytes: zod.int().describe('Stored size in bytes, -1 when the file is missing'),
                  })
                  .describe('One stored thumbnail')
              )
              .describe('Its thumbnails before'),
            after: zod
              .array(
                zod
                  .object({
                    type: zod.string().describe('Asset type'),
                    bytes: zod.int().describe('Stored size in bytes, -1 when the file is missing'),
                  })
                  .describe('One stored thumbnail')
              )
              .optional()
              .describe('Its thumbnails after, absent on a dry run'),
            outcome: zod.enum(['PENDING', 'REGENERATED', 'FAILED']).describe('What happened'),
            error: zod
              .string()
              .optional()
              .describe('Error message when the regeneration itself failed'),
          })
          .describe('A route, ride or trip whose thumbnails were (or would be) redrawn')
      )
      .describe('Entities processed, at most the limit'),
  })
  .describe('Outcome of a thumbnail regeneration, one entry per entity')
