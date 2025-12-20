import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
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
import { RideStatus, Visibility } from '../api/api'

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
export { RideStatus, Visibility }

interface UseRidesOptions {
  from?: string
  to?: string
  status?: RideStatus
  page?: number
  size?: number
}

export function useRides(teamSlug: string | undefined, options: UseRidesOptions = {}) {
  const { from, to, status, page = 0, size = 20 } = options

  return useQuery({
    queryKey: ['rides', teamSlug, { from, to, status, page, size }],
    queryFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(ridesApi.listRides(teamSlug, from, page, size, status, to))
    },
    enabled: !!teamSlug,
    staleTime: 1000 * 60 * 2,
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
    staleTime: 1000 * 60 * 2,
  })
}

export function useRideGroups(teamSlug: string | undefined, rideSlug: string | undefined) {
  return useQuery({
    queryKey: ['rideGroups', teamSlug, rideSlug],
    queryFn: async () => {
      if (!teamSlug || !rideSlug) throw new Error('Team slug and ride slug are required')
      return await unwrapResponse(ridesApi.listGroups(rideSlug, teamSlug))
    },
    enabled: !!teamSlug && !!rideSlug,
    staleTime: 1000 * 60 * 2,
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
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.rideCreated',
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
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.rideUpdated',
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
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.rideDeleted',
      })

      navigate(`/teams/${teamSlug}/rides`)
    },
  })
}

export function useJoinRide(teamSlug: string | undefined, rideSlug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async ({ groupId, notes }: { groupId: string; notes?: string }) => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(
        ridesApi.joinGroup(groupId, rideSlug, teamSlug, { notes: notes || null })
      )
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ride', teamSlug, rideSlug] })
      queryClient.invalidateQueries({ queryKey: ['rideGroups', teamSlug, rideSlug] })
      queryClient.invalidateQueries({ queryKey: ['rides', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.rideJoined',
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
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.rideLeft',
      })
    },
  })
}
