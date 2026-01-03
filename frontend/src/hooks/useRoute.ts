import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import i18next from 'i18next'
import { routesApi, unwrapResponse } from '../lib/apiClient'
import type {
  RouteDto,
  RouteDetailDto,
  ClimbDto,
  TrackDto,
  WaypointDto,
  RouteListResponse,
  RouteRequest,
  GeoPoint,
} from '../api/api'
import { SurfaceType, Hilliness, RouteSortBy, SortDirection, WindDirection } from '../api/api'
import { paths } from '@/config/paths'

// Re-export types for convenience
export type {
  RouteDto,
  RouteDetailDto,
  ClimbDto,
  TrackDto,
  WaypointDto,
  RouteListResponse,
  RouteRequest,
  GeoPoint,
}

// Re-export enums as values (not types)
export { SurfaceType, Hilliness, RouteSortBy, SortDirection, WindDirection }

// Filter parameters for route listing
export interface RouteFilters {
  search?: string
  minDistance?: number // in meters
  maxDistance?: number // in meters
  minElevationGain?: number // in meters
  maxElevationGain?: number // in meters
  hilliness?: (typeof Hilliness)[keyof typeof Hilliness]
  surfaceTypes?: (typeof SurfaceType)[keyof typeof SurfaceType][]
  windDirection?: (typeof WindDirection)[keyof typeof WindDirection]
  sortBy?: (typeof RouteSortBy)[keyof typeof RouteSortBy]
  sortDir?: (typeof SortDirection)[keyof typeof SortDirection]
  page?: number
  size?: number
}

export function useRoutes(teamSlug: string | undefined, options: RouteFilters = {}) {
  const {
    search,
    minDistance,
    maxDistance,
    minElevationGain,
    maxElevationGain,
    hilliness,
    surfaceTypes,
    windDirection,
    sortBy,
    sortDir,
    page = 0,
    size = 20,
  } = options

  return useQuery({
    queryKey: ['routes', teamSlug, options],
    queryFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')

      // Convert arrays to Sets for API call
      const surfaceTypesSet = surfaceTypes?.length ? new Set(surfaceTypes) : undefined
      const windDirectionsSet = windDirection ? new Set([windDirection]) : undefined

      return await unwrapResponse(
        routesApi.listRoutes(
          teamSlug,
          hilliness,
          maxDistance,
          maxElevationGain,
          minDistance,
          minElevationGain,
          undefined, // nearLat - deferred
          undefined, // nearLon - deferred
          undefined, // nearRadius - deferred
          undefined, // nearType - deferred
          page,
          search,
          size,
          sortBy,
          sortDir,
          surfaceTypesSet,
          windDirectionsSet
        )
      )
    },
    enabled: !!teamSlug,
    placeholderData: keepPreviousData,
  })
}

export function useAllRoutes(options: RouteFilters = {}) {
  const {
    search,
    minDistance,
    maxDistance,
    minElevationGain,
    maxElevationGain,
    hilliness,
    surfaceTypes,
    windDirection,
    sortBy,
    sortDir,
    page = 0,
    size = 20,
  } = options

  return useQuery({
    queryKey: ['routes', 'all', options],
    queryFn: async () => {
      // Convert arrays to Sets for API call
      const surfaceTypesSet = surfaceTypes?.length ? new Set(surfaceTypes) : undefined
      const windDirectionsSet = windDirection ? new Set([windDirection]) : undefined

      return await unwrapResponse(
        routesApi.listAllRoutes(
          hilliness,
          maxDistance,
          maxElevationGain,
          minDistance,
          minElevationGain,
          undefined, // nearLat - deferred
          undefined, // nearLon - deferred
          undefined, // nearRadius - deferred
          undefined, // nearType - deferred
          page,
          search,
          size,
          sortBy,
          sortDir,
          surfaceTypesSet,
          windDirectionsSet
        )
      )
    },
    placeholderData: keepPreviousData,
  })
}

export function useRoute(teamSlug: string | undefined, routeSlug: string | undefined) {
  return useQuery({
    queryKey: ['route', teamSlug, routeSlug],
    queryFn: async () => {
      if (!teamSlug || !routeSlug) throw new Error('Team slug and route slug are required')
      return await unwrapResponse(routesApi.getRoute(routeSlug, teamSlug))
    },
    enabled: !!teamSlug && !!routeSlug,
  })
}

export function useCreateRoute(teamSlug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (data: { route: RouteRequest; gpxFile?: File }) => {
      return await unwrapResponse(routesApi.createRoute(teamSlug, data.route, data.gpxFile))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['routes', teamSlug] })

      toast.success(i18next.t('routes:notifications.created'))
    },
  })
}

export function useUpdateRoute(teamSlug: string, routeSlug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (data: { route: RouteRequest; gpxFile?: File }) => {
      return await unwrapResponse(
        routesApi.updateRoute(routeSlug, teamSlug, data.route, data.gpxFile)
      )
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['route', teamSlug, routeSlug] })
      queryClient.invalidateQueries({ queryKey: ['routes', teamSlug] })

      toast.success(i18next.t('routes:notifications.updated'))
    },
  })
}

export function useDeleteRoute(teamSlug: string) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (routeSlug: string) => {
      await unwrapResponse(routesApi.deleteRoute(routeSlug, teamSlug))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['routes', teamSlug] })

      toast.success(i18next.t('routes:notifications.deleted'))

      navigate(paths.routes(teamSlug!))
    },
  })
}
