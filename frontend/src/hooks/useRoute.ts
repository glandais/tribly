import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { routesApi, unwrapResponse } from '../lib/apiClient'
import { useNotificationStore } from '../store/notificationStore'
import type {
  RouteDto,
  RouteDetailDto,
  RouteClimbDto,
  TrackPointDto,
  GpxTrackDto,
  RouteListResponse,
  ClimbListResponse,
  UpdateRouteRequest,
} from '../api/api'
import { RouteDifficulty, Visibility, SurfaceType } from '../api/api'

// Re-export types for convenience
export type {
  RouteDto,
  RouteDetailDto,
  RouteClimbDto,
  TrackPointDto,
  GpxTrackDto,
  RouteListResponse,
  ClimbListResponse,
  UpdateRouteRequest,
}

// Re-export enums as values (not types)
export { RouteDifficulty, SurfaceType }

export function useRoutes(teamSlug: string | undefined, page = 0, size = 20) {
  return useQuery({
    queryKey: ['routes', teamSlug, page, size],
    queryFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(routesApi.listRoutes(teamSlug, page, size))
    },
    enabled: !!teamSlug,
    staleTime: 1000 * 60 * 2,
  })
}

export function useRoute(teamSlug: string | undefined, routeId: string | undefined) {
  return useQuery({
    queryKey: ['route', teamSlug, routeId],
    queryFn: async () => {
      if (!teamSlug || !routeId) throw new Error('Team slug and route ID are required')
      return await unwrapResponse(routesApi.getRoute(routeId, teamSlug))
    },
    enabled: !!teamSlug && !!routeId,
    staleTime: 1000 * 60 * 2,
  })
}

export function useRouteClimbs(teamSlug: string | undefined, routeId: string | undefined) {
  return useQuery({
    queryKey: ['routeClimbs', teamSlug, routeId],
    queryFn: async () => {
      if (!teamSlug || !routeId) throw new Error('Team slug and route ID are required')
      return await unwrapResponse(routesApi.getClimbs(routeId, teamSlug))
    },
    enabled: !!teamSlug && !!routeId,
    staleTime: 1000 * 60 * 2,
  })
}

export function useGpxTrack(teamSlug: string | undefined, routeId: string | undefined) {
  return useQuery({
    queryKey: ['gpxTrack', teamSlug, routeId],
    queryFn: async () => {
      if (!teamSlug || !routeId) throw new Error('Team slug and route ID are required')
      return await unwrapResponse(routesApi.getTrack(routeId, teamSlug))
    },
    enabled: !!teamSlug && !!routeId,
    staleTime: 1000 * 60 * 5, // Track data is less likely to change
  })
}

export function useCreateRoute(teamSlug: string) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (data: {
      name: string
      description?: string
      difficulty?: RouteDifficulty
      surfaceType?: SurfaceType
      visibility?: Visibility
      gpxFile: File
    }) => {
      return await unwrapResponse(
        routesApi.createRoute(
          teamSlug,
          data.name,
          data.description,
          data.difficulty,
          data.surfaceType,
          data.visibility,
          data.gpxFile
        )
      )
    },
    onSuccess: (route) => {
      queryClient.invalidateQueries({ queryKey: ['routes', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.routeCreated',
      })

      if (route) {
        navigate(`/teams/${teamSlug}/routes/${route.id}`)
      }
    },
  })
}

export function useUpdateRoute(teamSlug: string, routeId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (data: UpdateRouteRequest) => {
      return await unwrapResponse(routesApi.updateRoute(routeId, teamSlug, data))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['route', teamSlug, routeId] })
      queryClient.invalidateQueries({ queryKey: ['routes', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.routeUpdated',
      })
    },
  })
}

export function useDeleteRoute(teamSlug: string) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (routeId: string) => {
      await unwrapResponse(routesApi.deleteRoute(routeId, teamSlug))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['routes', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.routeDeleted',
      })

      navigate(`/teams/${teamSlug}/routes`)
    },
  })
}
