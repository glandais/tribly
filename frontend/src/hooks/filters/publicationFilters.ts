import { z } from 'zod'
import { ListViewMode, PublicationType } from '@/api/dto'
import { COMMON_ALIAS, pageField, searchField, sizeField } from './common'

export const PUBLICATION_PAGE_SIZE = 12

export type PublicationFilterValue = 'all' | 'ride' | 'post' | 'trip'

/**
 * Mutually exclusive scopes of a feed. `me` is authenticated-only — the contract
 * says `participating` "yields nothing for an anonymous visitor".
 */
export type PublicationScopeValue = 'all' | 'upcoming' | 'me'

/** Projects a scope onto the `from` / `participating` parameters of the list endpoints. */
export function publicationScopeToParams(
  scope: PublicationScopeValue,
  nowIso: string
): { from?: string; participating?: boolean } {
  switch (scope) {
    case 'upcoming':
      return { from: nowIso }
    case 'me':
      return { participating: true }
    default:
      return {}
  }
}

export const publicationFilterToType: Record<PublicationFilterValue, PublicationType | undefined> =
  {
    all: undefined,
    ride: PublicationType.RIDE,
    post: PublicationType.POST,
    trip: PublicationType.TRIP,
  }

/** `filter` holds the page's own value, not `PublicationType`, so the URL stays readable. */
export const publicationFiltersSchema = z.object({
  search: searchField,
  filter: z.enum(['all', 'ride', 'post', 'trip']).default('all').catch('all'),
  scope: z.enum(['all', 'upcoming', 'me']).default('all').catch('all'),
  page: pageField,
  size: sizeField(PUBLICATION_PAGE_SIZE),
})

export const publicationFiltersAlias = { ...COMMON_ALIAS, filter: 'type', scope: 'w' } as const

/**
 * Projects the page's filters onto the list endpoint's params — the one place the two publication
 * lists (home feed, team feed) and their route `prefetch`es all go through.
 *
 * It exists because the prefetch has to produce a **byte-identical query key**, and it used to
 * rebuild the params by hand: `{ minRole, page, size, view }`. That worked only as long as the
 * defaults happened to project to nothing extra. Change `scope`'s default to `upcoming` and the
 * pages would start sending `from` while the prefetches wouldn't — the SSR cache silently missing
 * on the two most visited routes of the app, with nothing failing anywhere.
 *
 * `view` is always COMPACT here: a feed card draws a title, an excerpt and a picture, never the
 * markdown body or the attachments of twelve publications.
 */
export function publicationApiParams(
  filters: z.infer<typeof publicationFiltersSchema>,
  nowIso: string
) {
  return {
    search: filters.search,
    page: filters.page,
    size: filters.size,
    type: publicationFilterToType[filters.filter],
    ...publicationScopeToParams(filters.scope, nowIso),
    view: ListViewMode.COMPACT,
  }
}

/** The params a bare URL produces — what a route `prefetch` must fill the cache with. */
export function defaultPublicationApiParams(nowIso: string) {
  return publicationApiParams(publicationFiltersSchema.parse({}), nowIso)
}
