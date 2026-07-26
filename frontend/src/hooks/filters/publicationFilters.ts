import { z } from 'zod'
import { PublicationType } from '@/api/dto'
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
