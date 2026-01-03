import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import i18next from 'i18next'
import { teamPagesApi, unwrapResponse } from '../lib/apiClient'
import type {
  TeamPageDto,
  TeamPageSummaryDto,
  TeamPageRequest,
  ReorderPagesRequest,
} from '../api/api'
import { paths } from '@/config/paths'

// Re-export types for convenience
export type { TeamPageDto, TeamPageSummaryDto, TeamPageRequest }

export function useTeamPages(teamSlug: string | undefined) {
  return useQuery({
    queryKey: ['teamPages', teamSlug],
    queryFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(teamPagesApi.listPages(teamSlug))
    },
    enabled: !!teamSlug,
  })
}

export function useTeamPage(teamSlug: string | undefined, pageSlug: string | undefined) {
  return useQuery({
    queryKey: ['teamPage', teamSlug, pageSlug],
    queryFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')
      if (!pageSlug) throw new Error('Page slug is required')
      return await unwrapResponse(teamPagesApi.getPage(pageSlug, teamSlug))
    },
    enabled: !!teamSlug && !!pageSlug,
  })
}

export function useCreateTeamPage(teamSlug: string) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (data: TeamPageRequest) => {
      return await unwrapResponse(teamPagesApi.createPage(teamSlug, data))
    },
    onSuccess: (page) => {
      queryClient.invalidateQueries({ queryKey: ['teamPages', teamSlug] })
      queryClient.invalidateQueries({ queryKey: ['team', teamSlug] })

      toast.success(i18next.t('teams:pages.notifications.created'))

      if (page) {
        navigate(paths.teamAdminPages(teamSlug))
      }
    },
  })
}

export function useUpdateTeamPage(teamSlug: string, pageSlug: string) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (data: TeamPageRequest) => {
      return await unwrapResponse(teamPagesApi.updatePage(pageSlug, teamSlug, data))
    },
    onSuccess: (page) => {
      queryClient.invalidateQueries({ queryKey: ['teamPages', teamSlug] })
      queryClient.invalidateQueries({ queryKey: ['teamPage', teamSlug, pageSlug] })
      queryClient.invalidateQueries({ queryKey: ['team', teamSlug] })

      toast.success(i18next.t('teams:pages.notifications.updated'))

      if (page) {
        queryClient.setQueryData(['teamPage', teamSlug, page.slug], page)
        navigate(paths.teamAdminPages(teamSlug))
      }
    },
  })
}

export function useDeleteTeamPage(teamSlug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (pageSlug: string) => {
      await unwrapResponse(teamPagesApi.deletePage(pageSlug, teamSlug))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teamPages', teamSlug] })
      queryClient.invalidateQueries({ queryKey: ['team', teamSlug] })

      toast.success(i18next.t('teams:pages.notifications.deleted'))
    },
  })
}

export function useReorderTeamPages(teamSlug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (pageIds: string[]) => {
      const request: ReorderPagesRequest = { pageIds }
      return await unwrapResponse(teamPagesApi.reorderPages(teamSlug, request))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teamPages', teamSlug] })
      queryClient.invalidateQueries({ queryKey: ['team', teamSlug] })

      toast.success(i18next.t('teams:pages.notifications.reordered'))
    },
  })
}
