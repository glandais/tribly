import * as zod from 'zod'

/**
 * Public teams and their public content, as an anonymous visitor sees it, for the site the request arrived on (a pinned host lists its one team). Classified ads are never listed. Anonymous by construction: the caller's session does not widen it.
 * @summary Get the indexable pages of this site
 */
export const GetSitemapResponse = zod
  .object({
    entries: zod
      .array(
        zod
          .object({
            type: zod
              .enum([
                'TEAM',
                'TEAM_ABOUT',
                'TEAM_PAGE',
                'RIDE',
                'POST',
                'TRIP',
                'TRIP_STAGE',
                'ROUTE',
              ])
              .describe('Which page this is'),
            teamSlug: zod.string().describe('Slug of the team the page belongs to'),
            tripSlug: zod
              .string()
              .optional()
              .describe('Slug of the trip a TRIP_STAGE belongs to; null for every other type'),
            slug: zod
              .string()
              .optional()
              .describe('Slug of the page itself; null for TEAM and TEAM_ABOUT'),
            lastModified: zod.iso
              .datetime({ offset: true })
              .describe("Last modification of the page's content"),
          })
          .describe(
            "One indexable page. The API returns slugs, not URLs: the path is localised and belongs to the client's route table."
          )
      )
      .describe('Indexable pages'),
  })
  .describe(
    "Every page of the site a search engine may index: the public content of public teams, without classified ads. Capped at 50,000 entries, the sitemap protocol's limit; newest first within each type."
  )
