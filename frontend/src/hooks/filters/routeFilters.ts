import { z } from 'zod'
import { Hilliness, SurfaceType, WindDirection, RouteSortBy, SortDirection } from '@/api/dto'
import {
  DEFAULT_ROUTE_SORT_BY,
  DEFAULT_ROUTE_SORT_DIR,
} from '@/components/route/routeFilterDefaults'
import { COMMON_ALIAS, optionalNumberField, pageField, searchField, sizeField } from './common'
import {
  MEMBERSHIP_ALIAS,
  MEMBERSHIP_ALWAYS_SERIALIZE,
  membershipField,
  type MembershipFilterValue,
} from './membership'

export const ROUTE_PAGE_SIZE = 12

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
} as const

/** The cross-team route list adds a membership filter; a team's own list has no use for one. */
export const makeAllRouteFiltersSchema = (defaultMembership: MembershipFilterValue) =>
  routeFiltersSchema.extend({ membership: membershipField(defaultMembership) })

export const allRouteFiltersAlias = { ...routeFiltersAlias, ...MEMBERSHIP_ALIAS } as const

export const allRouteFiltersAlwaysSerialize = MEMBERSHIP_ALWAYS_SERIALIZE
