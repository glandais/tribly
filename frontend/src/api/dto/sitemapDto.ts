import type { SitemapEntryDto } from './sitemapEntryDto.ts'

/**
 * Every page of the site a search engine may index: the public content of public teams, without classified ads nor routes. Capped at 50,000 entries, the sitemap protocol's limit; newest first within each type.
 */
export interface SitemapDto {
  /** Indexable pages */
  entries: SitemapEntryDto[]
}
