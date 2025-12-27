import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { postsApi, unwrapResponse } from '../lib/apiClient'
import { useNotificationStore } from '../store/notificationStore'
import type { PostDto, PostListResponse, PostRequest } from '../api/api'
import { Status, Visibility } from '../api/api'

// Re-export types for convenience
export type { PostDto, PostListResponse, PostRequest }

// Re-export enums as values (not types)
export { Status, Visibility }

interface UsePostsOptions {
  search?: string
  from?: string
  to?: string
  page?: number
  size?: number
}

export function usePosts(teamSlug: string | undefined, options: UsePostsOptions = {}) {
  const { search, from, to, page = 0, size = 20 } = options

  return useQuery({
    queryKey: ['posts', teamSlug, { search, from, to, page, size }],
    queryFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(postsApi.listPosts(teamSlug, from, page, search, size, to))
    },
    enabled: !!teamSlug,
    placeholderData: keepPreviousData,
  })
}

export function usePost(teamSlug: string | undefined, postSlug: string | undefined) {
  return useQuery({
    queryKey: ['post', teamSlug, postSlug],
    queryFn: async () => {
      if (!teamSlug || !postSlug) throw new Error('Team slug and post slug are required')
      return await unwrapResponse(postsApi.getPost(postSlug, teamSlug))
    },
    enabled: !!teamSlug && !!postSlug,
  })
}

export function useCreatePost(teamSlug: string | undefined) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (data: PostRequest) => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(postsApi.createPost(teamSlug, data))
    },
    onSuccess: (post) => {
      queryClient.invalidateQueries({ queryKey: ['posts', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.postCreated',
      })

      if (post) {
        navigate(`/teams/${teamSlug}/posts/${post.slug}`)
      }
    },
  })
}

export function useUpdatePost(teamSlug: string | undefined, postSlug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (data: PostRequest) => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(postsApi.updatePost(postSlug, teamSlug, data))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['post', teamSlug, postSlug] })
      queryClient.invalidateQueries({ queryKey: ['posts', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.postUpdated',
      })
    },
  })
}

export function useDeletePost(teamSlug: string | undefined) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (postSlug: string) => {
      if (!teamSlug) throw new Error('Team slug is required')
      await unwrapResponse(postsApi.deletePost(postSlug, teamSlug))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['posts', teamSlug] })

      // Show success notification
      useNotificationStore.getState().addNotification({
        message: '',
        type: 'success',
        duration: 4000,
        translationKey: 'notifications.postDeleted',
      })

      navigate(`/teams/${teamSlug}/posts`)
    },
  })
}
