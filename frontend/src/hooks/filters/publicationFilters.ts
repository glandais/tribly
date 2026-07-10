import { z } from 'zod'
import { PublicationType } from '@/api/dto'
import { COMMON_ALIAS, pageField, searchField, sizeField } from './common'

export const PUBLICATION_PAGE_SIZE = 12

export type PublicationFilterValue = 'all' | 'ride' | 'post' | 'trip'

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
  page: pageField,
  size: sizeField(PUBLICATION_PAGE_SIZE),
})

export const publicationFiltersAlias = { ...COMMON_ALIAS, filter: 'type' } as const
