import { z } from 'zod'
import {
  Hilliness,
  ListViewMode,
  SurfaceType,
  WindDirection,
  RouteSortBy,
  SortDirection,
} from '@/api/dto'
import {
  DEFAULT_ROUTE_SORT_BY,
  DEFAULT_ROUTE_SORT_DIR,
} from '@/components/route/routeFilterDefaults'
import { COMMON_ALIAS, optionalNumberField, pageField, searchField, sizeField } from './common'
import {
  MEMBERSHIP_ALIAS,
  MEMBERSHIP_ALWAYS_SERIALIZE,
  membershipField,
  membershipToMinRole,
  type MembershipFilterValue,
} from './membership'

export const ROUTE_PAGE_SIZE = 12

/** Beyond this many results the list opens in the dense row density unless the user said otherwise. */
export const ROUTE_DENSITY_AUTO_THRESHOLD = 200

export type RouteDensity = 'card' | 'row'

/**
 * The density actually rendered: the user's explicit choice, else derived from the size of
 * the result set. Left `undefined` in the schema on purpose — a default would pin the URL to
 * `card` and defeat the automatic switch on a large library.
 */
export function resolveRouteDensity(
  chosen: RouteDensity | undefined,
  total: number | undefined
): RouteDensity {
  if (chosen) return chosen
  return (total ?? 0) > ROUTE_DENSITY_AUTO_THRESHOLD ? 'row' : 'card'
}

/** `ListRoutesParams` and `ListAllRoutesParams` are identical, so both route pages share this. */
export const routeFiltersSchema = z.object({
  search: searchField,
  minDistance: optionalNumberField,
  maxDistance: optionalNumberField,
  minElevationGain: optionalNumberField,
  maxElevationGain: optionalNumberField,
  hilliness: z.enum(Hilliness).optional().catch(undefined),
  surfaceType: z.enum(SurfaceType).optional().catch(undefined),
  windDirection: z.enum(WindDirection).optional().catch(undefined),
  sortBy: z.enum(RouteSortBy).default(DEFAULT_ROUTE_SORT_BY).catch(DEFAULT_ROUTE_SORT_BY),
  sortDir: z.enum(SortDirection).default(DEFAULT_ROUTE_SORT_DIR).catch(DEFAULT_ROUTE_SORT_DIR),
  page: pageField,
  size: sizeField(ROUTE_PAGE_SIZE),
  /** Presentation, not a query parameter — must be stripped before hitting the API. */
  density: z.enum(['card', 'row']).optional().catch(undefined),
})

export type RouteFilters = z.infer<typeof routeFiltersSchema>

export const routeFiltersAlias = {
  ...COMMON_ALIAS,
  minDistance: 'dmin',
  maxDistance: 'dmax',
  minElevationGain: 'emin',
  maxElevationGain: 'emax',
  hilliness: 'hill',
  surfaceType: 'surf',
  windDirection: 'wind',
  sortBy: 'sort',
  sortDir: 'dir',
  density: 'd',
} as const

/** The cross-team route list adds a membership filter; a team's own list has no use for one. */
export const makeAllRouteFiltersSchema = (defaultMembership: MembershipFilterValue) =>
  routeFiltersSchema.extend({ membership: membershipField(defaultMembership) })

export const allRouteFiltersAlias = { ...routeFiltersAlias, ...MEMBERSHIP_ALIAS } as const

export const allRouteFiltersAlwaysSerialize = MEMBERSHIP_ALWAYS_SERIALIZE

/**
 * Projects a team route list's filters onto the endpoint's params: `density` is presentation and
 * must never reach the API, `view` is always COMPACT (a card draws a name, a distance and a
 * thumbnail).
 *
 * Shared with the `routes` route's `prefetch`, which resolves the same filters out of the request's
 * query string — a link carrying `?q=col` has to prefetch *that* list, not the default one.
 */
export function routeApiParams(filters: RouteFilters) {
  const rest = { ...filters, view: ListViewMode.COMPACT }
  delete rest.density
  return rest
}

export type AllRouteFilters = z.infer<ReturnType<typeof makeAllRouteFiltersSchema>>

/** Same, for the cross-team list: `membership` is the page's own value, the API wants a MinRole. */
export function allRouteApiParams(filters: AllRouteFilters) {
  const { membership, ...withDensity } = filters
  const rest = {
    ...withDensity,
    minRole: membershipToMinRole[membership],
    view: ListViewMode.COMPACT,
  }
  delete rest.density
  return rest
}
