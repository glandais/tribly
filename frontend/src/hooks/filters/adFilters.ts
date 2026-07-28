import { z } from 'zod'
import { AdSortBy, AdType, SortDirection } from '@/api/dto'
import { DEFAULT_AD_SORT_BY, DEFAULT_AD_SORT_DIR } from '@/components/ad/adSortOptions'
import { COMMON_ALIAS, optionalNumberField, pageField, searchField, sizeField } from './common'

export const AD_PAGE_SIZE = 12

export const adFiltersSchema = z.object({
  search: searchField,
  adType: z.enum(AdType).optional().catch(undefined),
  /** Either bound excludes the ads with no price ("à négocier") — see `ListAdsParams`. */
  minPrice: optionalNumberField,
  maxPrice: optionalNumberField,
  sortBy: z.enum(AdSortBy).default(DEFAULT_AD_SORT_BY).catch(DEFAULT_AD_SORT_BY),
  sortDir: z.enum(SortDirection).default(DEFAULT_AD_SORT_DIR).catch(DEFAULT_AD_SORT_DIR),
  page: pageField,
  size: sizeField(AD_PAGE_SIZE),
})

export type AdFilters = z.infer<typeof adFiltersSchema>

export const adFiltersAlias = {
  ...COMMON_ALIAS,
  adType: 'type',
  minPrice: 'pmin',
  maxPrice: 'pmax',
  sortBy: 'sort',
  sortDir: 'dir',
} as const

/**
 * True as soon as a criterion *narrows* the result set. Sorting is not one: it cannot empty a
 * list, so it has no place in diagnosing a dead end — nor in what "clear filters" resets.
 */
export function isAdFiltered(filters: AdFilters): boolean {
  return (
    !!filters.search?.trim() ||
    !!filters.adType ||
    filters.minPrice !== undefined ||
    filters.maxPrice !== undefined
  )
}
