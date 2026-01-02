import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import i18next from 'i18next'
import {
  rideCommentsApi,
  postCommentsApi,
  tripCommentsApi,
  routeCommentsApi,
  unwrapResponse,
} from '../lib/apiClient'
import { useNotificationStore } from '../store/notificationStore'
import type { CommentDto, CommentListResponse, CommentRequest } from '../api/api'

// Re-export types for convenience
export type { CommentDto, CommentListResponse, CommentRequest }

export type EntityType = 'rides' | 'posts' | 'trips' | 'routes'

export function useComments(
  teamSlug: string | undefined,
  entityType: EntityType,
  entitySlug: string | undefined
) {
  return useQuery({
    queryKey: ['comments', teamSlug, entityType, entitySlug],
    queryFn: async () => {
      if (!teamSlug || !entitySlug) throw new Error('Team and entity slugs are required')
      switch (entityType) {
        case 'rides':
          return await unwrapResponse(rideCommentsApi.listRideComments(entitySlug, teamSlug))
        case 'posts':
          return await unwrapResponse(postCommentsApi.listPostComments(entitySlug, teamSlug))
        case 'trips':
          return await unwrapResponse(tripCommentsApi.listTripComments(entitySlug, teamSlug))
        case 'routes':
          return await unwrapResponse(routeCommentsApi.listRouteComments(entitySlug, teamSlug))
      }
    },
    enabled: !!teamSlug && !!entitySlug,
  })
}

export function useCreateComment(
  teamSlug: string | undefined,
  entityType: EntityType,
  entitySlug: string | undefined
) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (data: CommentRequest) => {
      if (!teamSlug || !entitySlug) throw new Error('Team and entity slugs are required')
      switch (entityType) {
        case 'rides':
          return await unwrapResponse(rideCommentsApi.createRideComment(entitySlug, teamSlug, data))
        case 'posts':
          return await unwrapResponse(postCommentsApi.createPostComment(entitySlug, teamSlug, data))
        case 'trips':
          return await unwrapResponse(tripCommentsApi.createTripComment(entitySlug, teamSlug, data))
        case 'routes':
          return await unwrapResponse(
            routeCommentsApi.createRouteComment(entitySlug, teamSlug, data)
          )
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['comments', teamSlug, entityType, entitySlug] })
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('comments:notifications.created'),
        duration: 4000,
      })
    },
  })
}

export function useDeleteComment(
  teamSlug: string | undefined,
  entityType: EntityType,
  entitySlug: string | undefined
) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (commentId: string) => {
      if (!teamSlug || !entitySlug) throw new Error('Team and entity slugs are required')
      switch (entityType) {
        case 'rides':
          return await unwrapResponse(
            rideCommentsApi.deleteRideComment(commentId, entitySlug, teamSlug)
          )
        case 'posts':
          return await unwrapResponse(
            postCommentsApi.deletePostComment(commentId, entitySlug, teamSlug)
          )
        case 'trips':
          return await unwrapResponse(
            tripCommentsApi.deleteTripComment(commentId, entitySlug, teamSlug)
          )
        case 'routes':
          return await unwrapResponse(
            routeCommentsApi.deleteRouteComment(commentId, entitySlug, teamSlug)
          )
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['comments', teamSlug, entityType, entitySlug] })
      useNotificationStore.getState().addNotification({
        type: 'success',
        translatedMessage: i18next.t('comments:notifications.deleted'),
        duration: 4000,
      })
    },
  })
}
