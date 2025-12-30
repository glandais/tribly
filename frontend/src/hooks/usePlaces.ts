import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import i18next from 'i18next'
import { placesApi, unwrapResponse } from '../lib/apiClient'
import { useNotificationStore } from '../store/notificationStore'
import type { PlaceDetailDto, PlaceRequest } from '../api/api'

// Re-export types for convenience
export type { PlaceDetailDto, PlaceRequest }

export function usePlaces(teamSlug: string | undefined, page = 0, size = 50) {
  return useQuery({
    queryKey: ['places', teamSlug, page, size],
    queryFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(placesApi.listPlaces(teamSlug, page, size))
    },
    enabled: !!teamSlug,
  })
}

export function usePlace(teamSlug: string | undefined, placeId: string | undefined) {
  return useQuery({
    queryKey: ['place', teamSlug, placeId],
    queryFn: async () => {
      if (!teamSlug || !placeId) throw new Error('Team slug and place ID required')
      return await unwrapResponse(placesApi.getPlace(teamSlug, placeId))
    },
    enabled: !!teamSlug && !!placeId,
  })
}

export function useCreatePlace(teamSlug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (data: PlaceRequest) => {
      return await unwrapResponse(placesApi.createPlace(teamSlug, data))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['places', teamSlug] })
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('teams:notifications.placeCreated'),
        duration: 4000,
      })
    },
  })
}

export function useUpdatePlace(teamSlug: string, placeId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (data: PlaceRequest) => {
      return await unwrapResponse(placesApi.updatePlace(teamSlug, placeId, data))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['places', teamSlug] })
      queryClient.invalidateQueries({ queryKey: ['place', teamSlug, placeId] })
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('teams:notifications.placeUpdated'),
        duration: 4000,
      })
    },
  })
}

export function useDeletePlace(teamSlug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (placeId: string) => {
      await unwrapResponse(placesApi.deletePlace(teamSlug, placeId))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['places', teamSlug] })
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('teams:notifications.placeDeleted'),
        duration: 4000,
      })
    },
  })
}
