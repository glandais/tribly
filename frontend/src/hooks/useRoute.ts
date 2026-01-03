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
import { SurfaceType } from '../api/api'
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
export { SurfaceType }

export function useRoutes(teamSlug: string | undefined, page = 0, size = 20, search?: string) {
  return useQuery({
    queryKey: ['routes', teamSlug, page, size, search],
    queryFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(routesApi.listRoutes(teamSlug, page, search, size))
    },
    enabled: !!teamSlug,
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
