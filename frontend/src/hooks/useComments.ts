import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import {
  listRideComments,
  createRideComment,
  deleteRideComment,
  getListRideCommentsQueryKey,
} from '../api/endpoints/ride-comments/ride-comments'
import {
  listPostComments,
  createPostComment,
  deletePostComment,
  getListPostCommentsQueryKey,
} from '../api/endpoints/post-comments/post-comments'
import {
  listTripComments,
  createTripComment,
  deleteTripComment,
  getListTripCommentsQueryKey,
} from '../api/endpoints/trip-comments/trip-comments'
import {
  listRouteComments,
  createRouteComment,
  deleteRouteComment,
  getListRouteCommentsQueryKey,
} from '../api/endpoints/route-comments/route-comments'
import type { CommentDto, CommentListResponse, CommentRequest } from '@/api/dto'

// Re-export types for convenience
export type { CommentDto, CommentListResponse, CommentRequest }

export type EntityType = 'rides' | 'posts' | 'trips' | 'routes'

function getQueryKey(teamSlug: string, entityType: EntityType, entitySlug: string) {
  switch (entityType) {
    case 'rides':
      return getListRideCommentsQueryKey(teamSlug, entitySlug)
    case 'posts':
      return getListPostCommentsQueryKey(teamSlug, entitySlug)
    case 'trips':
      return getListTripCommentsQueryKey(teamSlug, entitySlug)
    case 'routes':
      return getListRouteCommentsQueryKey(teamSlug, entitySlug)
  }
}

export function useComments(
  teamSlug: string | undefined,
  entityType: EntityType,
  entitySlug: string | undefined
) {
  return useQuery({
    queryKey: getQueryKey(teamSlug!, entityType, entitySlug!),
    queryFn: async () => {
      switch (entityType) {
        case 'rides':
          return await listRideComments(teamSlug!, entitySlug!)
        case 'posts':
          return await listPostComments(teamSlug!, entitySlug!)
        case 'trips':
          return await listTripComments(teamSlug!, entitySlug!)
        case 'routes':
          return await listRouteComments(teamSlug!, entitySlug!)
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
          return await createRideComment(teamSlug, entitySlug, data)
        case 'posts':
          return await createPostComment(teamSlug, entitySlug, data)
        case 'trips':
          return await createTripComment(teamSlug, entitySlug, data)
        case 'routes':
          return await createRouteComment(teamSlug, entitySlug, data)
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: getQueryKey(teamSlug!, entityType, entitySlug!) })
      toast.success(i18next.t('comments:notifications.created'))
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
          return await deleteRideComment(teamSlug, entitySlug, commentId)
        case 'posts':
          return await deletePostComment(teamSlug, entitySlug, commentId)
        case 'trips':
          return await deleteTripComment(teamSlug, entitySlug, commentId)
        case 'routes':
          return await deleteRouteComment(teamSlug, entitySlug, commentId)
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: getQueryKey(teamSlug!, entityType, entitySlug!) })
      toast.success(i18next.t('comments:notifications.deleted'))
    },
  })
}
