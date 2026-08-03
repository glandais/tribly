import type { ListPlacesParams } from '@/api/dto'

/**
 * The params `PlaceAutocomplete` queries with — and, called with no `search`, exactly the query it
 * runs on mount, before the visitor types anything. A ride/trip form mounts two of them (start and
 * end places), which is what the `ride-new`/`ride-edit` prefetch reproduces.
 *
 * One function rather than a constant mirrored in two places: the component and the route prefetch
 * must agree byte for byte or the prefetched entry is dead weight, and a duplicated literal would
 * drift the first time either side changes.
 *
 * Its own module, not an export of `PlaceAutocomplete.tsx`: `routes.config.ts` is eagerly imported
 * by both entries, so importing from the component would pull it into the main bundle.
 */
export function placeAutocompleteParams(opts: {
  filterStart?: boolean
  filterEnd?: boolean
  search?: string
}): ListPlacesParams {
  return {
    search: opts.search || undefined,
    filterStart: opts.filterStart || undefined,
    filterEnd: opts.filterEnd || undefined,
    size: 20,
  }
}
