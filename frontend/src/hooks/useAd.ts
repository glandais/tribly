import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import i18next from 'i18next'
import { adsApi, unwrapResponse } from '../lib/apiClient'
import type { AdDto, AdListResponse, AdRequest, AdType } from '../api/api'
import { Status, Visibility } from '../api/api'
import { paths } from '@/config/paths'

// Re-export types for convenience
export type { AdDto, AdListResponse, AdRequest, AdType }

// Re-export enums as values (not types)
export { Status, Visibility }

interface UseAdsOptions {
  search?: string
  adType?: AdType
  from?: string
  to?: string
  page?: number
  size?: number
}

export function useAds(teamSlug: string | undefined, options: UseAdsOptions = {}) {
  const { search, adType, from, to, page = 0, size = 20 } = options

  return useQuery({
    queryKey: ['ads', teamSlug, { search, adType, from, to, page, size }],
    queryFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(adsApi.listAds(teamSlug, adType, from, page, search, size, to))
    },
    enabled: !!teamSlug,
    placeholderData: keepPreviousData,
  })
}

export function useAd(teamSlug: string | undefined, adSlug: string | undefined) {
  return useQuery({
    queryKey: ['ad', teamSlug, adSlug],
    queryFn: async () => {
      if (!teamSlug || !adSlug) throw new Error('Team slug and ad slug are required')
      return await unwrapResponse(adsApi.getAd(adSlug, teamSlug))
    },
    enabled: !!teamSlug && !!adSlug,
  })
}

export function useCreateAd(teamSlug: string | undefined) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (data: AdRequest) => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(adsApi.createAd(teamSlug, data))
    },
    onSuccess: (ad) => {
      queryClient.invalidateQueries({ queryKey: ['ads', teamSlug] })

      toast.success(i18next.t('ads:notifications.created'))

      if (ad) {
        navigate(paths.ad(teamSlug!, ad.slug))
      }
    },
  })
}

export function useUpdateAd(teamSlug: string | undefined, adSlug: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (data: AdRequest) => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(adsApi.updateAd(adSlug, teamSlug, data))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ad', teamSlug, adSlug] })
      queryClient.invalidateQueries({ queryKey: ['ads', teamSlug] })

      toast.success(i18next.t('ads:notifications.updated'))
    },
  })
}

export function useDeleteAd(teamSlug: string | undefined) {
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  return useMutation({
    mutationFn: async (adSlug: string) => {
      if (!teamSlug) throw new Error('Team slug is required')
      await unwrapResponse(adsApi.deleteAd(adSlug, teamSlug))
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ads', teamSlug] })

      toast.success(i18next.t('ads:notifications.deleted'))

      navigate(paths.ads(teamSlug!))
    },
  })
}
