import type { SitemapEntryDto } from '@/api/dto'
import { SitemapEntryType } from '@/api/dto'
import { pathVariants } from '@/config/paths'

/**
 * The French path of a sitemap entry. French because it is the site's default language — the one
 * a crawler, which sends no cookie and usually no Accept-Language, is served — and one path per
 * page because both locale variants render the same page: listing the two would announce every
 * page twice.
 *
 * Map pages (`…/carte`) and ads have no entry type, so they can never be listed.
 */
export function sitemapPath(entry: SitemapEntryDto): string | null {
  const { teamSlug, tripSlug, slug } = entry
  switch (entry.type) {
    case SitemapEntryType.TEAM:
      return pathVariants.team(teamSlug).fr
    case SitemapEntryType.TEAM_ABOUT:
      return pathVariants.teamAbout(teamSlug).fr
    case SitemapEntryType.TEAM_PAGE:
      return slug ? pathVariants.teamPage(teamSlug, slug).fr : null
    case SitemapEntryType.RIDE:
      return slug ? pathVariants.ride(teamSlug, slug).fr : null
    case SitemapEntryType.POST:
      return slug ? pathVariants.post(teamSlug, slug).fr : null
    case SitemapEntryType.TRIP:
      return slug ? pathVariants.trip(teamSlug, slug).fr : null
    case SitemapEntryType.TRIP_STAGE:
      return slug && tripSlug ? pathVariants.stage(teamSlug, tripSlug, slug).fr : null
    case SitemapEntryType.ROUTE:
      return slug ? pathVariants.route(teamSlug, slug).fr : null
    default:
      // A type added to the contract after this build: skip it rather than guess its URL.
      return null
  }
}

function escapeXml(value: string): string {
  return value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&apos;')
}

/**
 * The `sitemap.xml` document for `origin` (e.g. `https://www.pedalons.fr`).
 *
 * @param toBrowser maps a router path to the one the address bar shows — the identity on a regular
 *   host, the prefix strip on a host pinned to one team (`pinnedHistory.toBrowser`).
 */
export function buildSitemapXml(
  entries: SitemapEntryDto[],
  origin: string,
  toBrowser: (path: string) => string = (path) => path
): string {
  const seen = new Set<string>()
  const urls: string[] = []
  const add = (path: string, lastModified?: string) => {
    const loc = origin + toBrowser(path)
    if (seen.has(loc)) return
    seen.add(loc)
    const lastmod = lastModified ? `<lastmod>${escapeXml(lastModified)}</lastmod>` : ''
    urls.push(`<url><loc>${escapeXml(loc)}</loc>${lastmod}</url>`)
  }

  add('/')
  for (const entry of entries) {
    const path = sitemapPath(entry)
    if (path) add(path, entry.lastModified)
  }

  return (
    '<?xml version="1.0" encoding="UTF-8"?>\n' +
    '<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n' +
    urls.join('\n') +
    '\n</urlset>\n'
  )
}
