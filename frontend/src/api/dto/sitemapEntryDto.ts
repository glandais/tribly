import type { Instant } from './instant.ts'
import type { SitemapEntryType } from './sitemapEntryType.ts'

/**
 * One indexable page. The API returns slugs, not URLs: the path is localised and belongs to the client's route table.
 */
export interface SitemapEntryDto {
  /** Which page this is */
  type: SitemapEntryType
  /** Slug of the team the page belongs to */
  teamSlug: string
  /** Slug of the trip a TRIP_STAGE belongs to; null for every other type */
  tripSlug?: string
  /** Slug of the page itself; null for TEAM and TEAM_ABOUT */
  slug?: string
  /** Last modification of the page's content */
  lastModified: Instant
}
