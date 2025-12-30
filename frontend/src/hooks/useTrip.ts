import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import i18next from 'i18next'
import { tripsApi, unwrapResponse } from '../lib/apiClient'
import { useNotificationStore } from '../store/notificationStore'
import type {
  TripDto,
  TripStageDto,
  TripParticipationDto,
  TripListResponse,
  TripRequest,
  StageRequest,
} from '../api/api'
import { Status, Visibility } from '../api/api'

// Re-export types for convenience
export type {
  TripDto,
  TripStageDto,
  TripParticipationDto,
  TripListResponse,
  TripRequest,
  StageRequest,
}

// Re-export enums as values (not types)
export { Status, Visibility }

interface UseTripsOptions {
  search?: string
  from?: string
  to?: string
  page?: number
  size?: number
}

export function useTrips(teamSlug: string | undefined, options: UseTripsOptions = {}) {
  const { search, from, to, page = 0, size = 20 } = options

  return useQuery({
    queryKey: ['trips', teamSlug, { search, from, to, page, size }],
    queryFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(tripsApi.listTrips(teamSlug, from, page, search, size, to))
    },
    enabled: !!teamSlug,
    placeholderData: keepPreviousData,
  })
}

export function useTrip(teamSlug: string | undefined, tripSlug: string | undefined) {
  return useQuery({
    queryKey: ['trip', teamSlug, tripSlug],
    queryFn: async () => {
      if (!teamSlug || !tripSlug) throw new Error('Team slug and trip slug are required')
      return await unwrapResponse(tripsApi.getTrip(teamSlug, tripSlug))
    },
    enabled: !!teamSlug && !!tripSlug,
  })
}

export function useCreateTrip(teamSlug: string | undefined) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (data: TripRequest) => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(tripsApi.createTrip(teamSlug, data))
    },
    onSuccess: (trip) => {
      queryClient.invalidateQueries({ queryKey: ['trips', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('trips:notifications.created'),
        duration: 4000,
      })

      if (trip) {
        navigate(`/teams/${teamSlug}/trips/${trip.slug}`)
      }
    },
  })
}

export function useUpdateTrip(teamSlug: string | undefined, tripSlug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (data: TripRequest) => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(tripsApi.updateTrip(teamSlug, tripSlug, data))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['trip', teamSlug, tripSlug] })
      queryClient.invalidateQueries({ queryKey: ['trips', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('trips:notifications.updated'),
        duration: 4000,
      })
    },
  })
}

export function useDeleteTrip(teamSlug: string | undefined) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (tripSlug: string) => {
      if (!teamSlug) throw new Error('Team slug is required')
      await unwrapResponse(tripsApi.deleteTrip(teamSlug, tripSlug))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['trips', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('trips:notifications.deleted'),
        duration: 4000,
      })

      navigate(`/teams/${teamSlug}/trips`)
    },
  })
}

export function useJoinTrip(teamSlug: string | undefined, tripSlug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(tripsApi.joinTrip(teamSlug, tripSlug))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['trip', teamSlug, tripSlug] })
      queryClient.invalidateQueries({ queryKey: ['trips', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('trips:notifications.joined'),
        duration: 4000,
      })
    },
  })
}

export function useLeaveTrip(teamSlug: string | undefined, tripSlug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')
      await unwrapResponse(tripsApi.leaveTrip(teamSlug, tripSlug))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['trip', teamSlug, tripSlug] })
      queryClient.invalidateQueries({ queryKey: ['trips', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('trips:notifications.left'),
        duration: 4000,
      })
    },
  })
}
