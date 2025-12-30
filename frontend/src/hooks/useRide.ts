import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import i18next from 'i18next'
import { ridesApi, unwrapResponse } from '../lib/apiClient'
import { useNotificationStore } from '../store/notificationStore'
import type {
  RideDto,
  RideGroupDto,
  RideParticipationDto,
  RideListResponse,
  RideGroupListResponse,
  RideRequest,
  GroupRequest,
} from '../api/api'
import { Status, Visibility } from '../api/api'

// Re-export types for convenience
export type {
  RideDto,
  RideGroupDto,
  RideParticipationDto,
  RideListResponse,
  RideGroupListResponse,
  RideRequest,
  GroupRequest,
}

// Re-export enums as values (not types)
export { Status, Visibility }

interface UseRidesOptions {
  search?: string
  from?: string
  to?: string
  page?: number
  size?: number
}

export function useRides(teamSlug: string | undefined, options: UseRidesOptions = {}) {
  const { search, from, to, page = 0, size = 20 } = options

  return useQuery({
    queryKey: ['rides', teamSlug, { search, from, to, page, size }],
    queryFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(ridesApi.listRides(teamSlug, from, page, search, size, to))
    },
    enabled: !!teamSlug,
    placeholderData: keepPreviousData,
  })
}

export function useRide(teamSlug: string | undefined, rideSlug: string | undefined) {
  return useQuery({
    queryKey: ['ride', teamSlug, rideSlug],
    queryFn: async () => {
      if (!teamSlug || !rideSlug) throw new Error('Team slug and ride slug are required')
      return await unwrapResponse(ridesApi.getRide(rideSlug, teamSlug))
    },
    enabled: !!teamSlug && !!rideSlug,
  })
}

export function useCreateRide(teamSlug: string | undefined) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (data: RideRequest) => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(ridesApi.createRide(teamSlug, data))
    },
    onSuccess: (ride) => {
      queryClient.invalidateQueries({ queryKey: ['rides', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('rides:notifications.created'),
        duration: 4000,
      })

      if (ride) {
        navigate(`/teams/${teamSlug}/rides/${ride.slug}`)
      }
    },
  })
}

export function useUpdateRide(teamSlug: string | undefined, rideSlug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (data: RideRequest) => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(ridesApi.updateRide(rideSlug, teamSlug, data))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamSlug, rideSlug] })
      queryClient.invalidateQueries({ queryKey: ['rides', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('rides:notifications.updated'),
        duration: 4000,
      })
    },
  })
}

export function useDeleteRide(teamSlug: string | undefined) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (rideSlug: string) => {
      if (!teamSlug) throw new Error('Team slug is required')
      await unwrapResponse(ridesApi.deleteRide(rideSlug, teamSlug))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rides', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('rides:notifications.deleted'),
        duration: 4000,
      })

      navigate(`/teams/${teamSlug}/rides`)
    },
  })
}

export function useJoinRide(teamSlug: string | undefined, rideSlug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async ({ groupId }: { groupId: string }) => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(ridesApi.joinGroup(groupId, rideSlug, teamSlug))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamSlug, rideSlug] })
      queryClient.invalidateQueries({ queryKey: ['rideGroups', teamSlug, rideSlug] })
      queryClient.invalidateQueries({ queryKey: ['rides', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('rides:notifications.joined'),
        duration: 4000,
      })
    },
  })
}

export function useLeaveRide(teamSlug: string | undefined, rideSlug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (groupId: string) => {
      if (!teamSlug) throw new Error('Team slug is required')
      await unwrapResponse(ridesApi.leaveGroup(groupId, rideSlug, teamSlug))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamSlug, rideSlug] })
      queryClient.invalidateQueries({ queryKey: ['rideGroups', teamSlug, rideSlug] })
      queryClient.invalidateQueries({ queryKey: ['rides', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('rides:notifications.left'),
        duration: 4000,
      })
    },
  })
}
