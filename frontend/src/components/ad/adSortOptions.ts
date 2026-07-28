import { AdSortBy, SortDirection } from '@/api/dto'

/** The server's own defaults, repeated so the sort control tells the truth before the first call. */
export const DEFAULT_AD_SORT_BY = AdSortBy.DATE_TIME
export const DEFAULT_AD_SORT_DIR = SortDirection.DESC

/**
 * The six sorts of an ad list — three columns, two directions each, flattened.
 *
 * They are enumerated flat rather than as "column + direction" because that is how one picks
 * them: nobody thinks "price, ascending", one thinks "the cheapest first". The label therefore
 * carries the direction, and the control is a single exclusive list.
 */
export const AD_SORT_OPTIONS = [
  { value: 'newest', sortBy: AdSortBy.DATE_TIME, sortDir: SortDirection.DESC },
  { value: 'oldest', sortBy: AdSortBy.DATE_TIME, sortDir: SortDirection.ASC },
  { value: 'priceAsc', sortBy: AdSortBy.PRICE, sortDir: SortDirection.ASC },
  { value: 'priceDesc', sortBy: AdSortBy.PRICE, sortDir: SortDirection.DESC },
  { value: 'nameAsc', sortBy: AdSortBy.NAME, sortDir: SortDirection.ASC },
  { value: 'nameDesc', sortBy: AdSortBy.NAME, sortDir: SortDirection.DESC },
] as const

export type AdSortOptionValue = (typeof AD_SORT_OPTIONS)[number]['value']

/**
 * The option matching a column/direction pair, `newest` otherwise — which is also the server's
 * default, so the control never claims an order the list isn't in.
 */
export function adSortOptionOf(sortBy: AdSortBy, sortDir: SortDirection): AdSortOptionValue {
  return (
    AD_SORT_OPTIONS.find((option) => option.sortBy === sortBy && option.sortDir === sortDir)
      ?.value ?? 'newest'
  )
}

export function adSortOptionByValue(value: string) {
  return AD_SORT_OPTIONS.find((option) => option.value === value)
}
