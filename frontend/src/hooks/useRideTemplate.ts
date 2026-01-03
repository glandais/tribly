import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import i18next from 'i18next'
import { rideTemplatesApi, unwrapResponse } from '../lib/apiClient'
import type {
  RideTemplateDto,
  RideTemplateGroupDto,
  RideTemplateListResponse,
  RideTemplateRequest,
  RideTemplateGroupRequest,
} from '../api/api'
import { paths } from '@/config/paths'

// Re-export types for convenience
export type {
  RideTemplateDto,
  RideTemplateGroupDto,
  RideTemplateListResponse,
  RideTemplateRequest,
  RideTemplateGroupRequest,
}

interface UseRideTemplatesOptions {
  search?: string
  page?: number
  size?: number
}

export function useRideTemplates(
  teamSlug: string | undefined,
  options: UseRideTemplatesOptions = {}
) {
  const { search, page = 0, size = 20 } = options

  return useQuery({
    queryKey: ['rideTemplates', teamSlug, { search, page, size }],
    queryFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(rideTemplatesApi.listTemplates(teamSlug, page, search, size))
    },
    enabled: !!teamSlug,
    placeholderData: keepPreviousData,
  })
}

export function useRideTemplate(teamSlug: string | undefined, templateSlug: string | undefined) {
  return useQuery({
    queryKey: ['rideTemplate', teamSlug, templateSlug],
    queryFn: async () => {
      if (!teamSlug || !templateSlug) throw new Error('Team slug and template slug are required')
      return await unwrapResponse(rideTemplatesApi.getTemplate(teamSlug, templateSlug))
    },
    enabled: !!teamSlug && !!templateSlug,
  })
}

export function useCreateRideTemplate(teamSlug: string | undefined) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (data: RideTemplateRequest) => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(rideTemplatesApi.createTemplate(teamSlug, data))
    },
    onSuccess: (template) => {
      queryClient.invalidateQueries({ queryKey: ['rideTemplates', teamSlug] })

      toast.success(i18next.t('rideTemplates:notifications.created'))

      if (template) {
        navigate(paths.rideTemplates(teamSlug!))
      }
    },
  })
}

export function useUpdateRideTemplate(teamSlug: string | undefined, templateSlug: string) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (data: RideTemplateRequest) => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(rideTemplatesApi.updateTemplate(teamSlug, templateSlug, data))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rideTemplate', teamSlug, templateSlug] })
      queryClient.invalidateQueries({ queryKey: ['rideTemplates', teamSlug] })

      toast.success(i18next.t('rideTemplates:notifications.updated'))

      navigate(paths.rideTemplates(teamSlug!))
    },
  })
}

export function useDeleteRideTemplate(teamSlug: string | undefined) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (templateSlug: string) => {
      if (!teamSlug) throw new Error('Team slug is required')
      await unwrapResponse(rideTemplatesApi.deleteTemplate(teamSlug, templateSlug))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rideTemplates', teamSlug] })

      toast.success(i18next.t('rideTemplates:notifications.deleted'))
    },
  })
}
