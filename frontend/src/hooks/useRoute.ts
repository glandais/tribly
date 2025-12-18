import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import apiClient from '../api/client'

export interface Route {
  id: string
  name: string
  description: string | null
  distance: number
  elevationGain: number
  elevationLoss: number
  difficulty: 'EASY' | 'MODERATE' | 'HARD' | 'EXPERT' | null
  surfaceType: 'ROAD' | 'GRAVEL' | 'MTB' | 'MIXED' | null
  isPublic: boolean
  thumbnailUrl: string | null
  createdAt: string
}

export interface RouteDetail extends Route {
  startLat: number | null
  startLng: number | null
  endLat: number | null
  endLng: number | null
  createdById: string
  updatedAt: string
}

export interface RouteClimb {
  id: string
  name: string | null
  startDistance: number
  endDistance: number
  elevationGain: number
  averageGradient: number
  maxGradient: number
  category: 'HC' | 'CAT1' | 'CAT2' | 'CAT3' | 'CAT4' | null
}

export interface TrackPoint {
  lat: number
  lng: number
  ele: number
  dist: number
}

export interface GpxTrack {
  id: string
  name: string
  trackPoints: TrackPoint[]
  processedAt: string
}

export interface RouteListResponse {
  routes: Route[]
  total: number
  page: number
  size: number
}

export interface ClimbListResponse {
  climbs: RouteClimb[]
}

export function useRoutes(teamSlug: string | undefined, page = 0, size = 20) {
  return useQuery({
    queryKey: ['routes', teamSlug, page, size],
    queryFn: async () => {
      return apiClient.get<RouteListResponse>(`/teams/${teamSlug}/routes`, {
        params: { page, size },
      })
    },
    enabled: !!teamSlug,
    staleTime: 1000 * 60 * 2,
  })
}

export function useRoute(teamSlug: string | undefined, routeId: string | undefined) {
  return useQuery({
    queryKey: ['route', teamSlug, routeId],
    queryFn: async () => {
      return apiClient.get<RouteDetail>(`/teams/${teamSlug}/routes/${routeId}`)
    },
    enabled: !!teamSlug && !!routeId,
    staleTime: 1000 * 60 * 2,
  })
}

export function useRouteClimbs(teamSlug: string | undefined, routeId: string | undefined) {
  return useQuery({
    queryKey: ['routeClimbs', teamSlug, routeId],
    queryFn: async () => {
      return apiClient.get<ClimbListResponse>(`/teams/${teamSlug}/routes/${routeId}/climbs`)
    },
    enabled: !!teamSlug && !!routeId,
    staleTime: 1000 * 60 * 2,
  })
}

export function useGpxTrack(teamSlug: string | undefined, routeId: string | undefined) {
  return useQuery({
    queryKey: ['gpxTrack', teamSlug, routeId],
    queryFn: async () => {
      return apiClient.get<GpxTrack>(`/teams/${teamSlug}/routes/${routeId}/track`)
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
      difficulty?: 'EASY' | 'MODERATE' | 'HARD' | 'EXPERT'
      surfaceType?: 'ROAD' | 'GRAVEL' | 'MTB' | 'MIXED'
      isPublic?: boolean
      gpxFile: File
    }) => {
      const formData = new FormData()
      formData.append('name', data.name)
      if (data.description) formData.append('description', data.description)
      if (data.difficulty) formData.append('difficulty', data.difficulty)
      if (data.surfaceType) formData.append('surfaceType', data.surfaceType)
      if (data.isPublic !== undefined) formData.append('isPublic', String(data.isPublic))
      formData.append('gpxFile', data.gpxFile)

      return apiClient.post<Route>(`/teams/${teamSlug}/routes`, formData)
    },
    onSuccess: (route) => {
      queryClient.invalidateQueries({ queryKey: ['routes', teamSlug] })
      navigate(`/teams/${teamSlug}/routes/${route.id}`)
    },
  })
}

export function useUpdateRoute(teamSlug: string, routeId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (data: {
      name?: string
      description?: string
      difficulty?: 'EASY' | 'MODERATE' | 'HARD' | 'EXPERT'
      surfaceType?: 'ROAD' | 'GRAVEL' | 'MTB' | 'MIXED'
      isPublic?: boolean
    }) => {
      return apiClient.patch<Route>(`/teams/${teamSlug}/routes/${routeId}`, data)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['route', teamSlug, routeId] })
      queryClient.invalidateQueries({ queryKey: ['routes', teamSlug] })
    },
  })
}

export function useDeleteRoute(teamSlug: string) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (routeId: string) => {
      return apiClient.delete(`/teams/${teamSlug}/routes/${routeId}`)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['routes', teamSlug] })
      navigate(`/teams/${teamSlug}/routes`)
    },
  })
}
