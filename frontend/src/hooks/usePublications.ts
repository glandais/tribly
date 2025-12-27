import { useQuery, keepPreviousData } from '@tanstack/react-query'
import { publicationsApi, unwrapResponse } from '../lib/apiClient'
import type { PublicationListResponse } from '../api/api'

// Re-export types for convenience
export type { PublicationListResponse }

interface UsePublicationsOptions {
  search?: string
  from?: string
  to?: string
  page?: number
  size?: number
}

export function usePublications(
  teamSlug: string | undefined,
  options: UsePublicationsOptions = {}
) {
  const { search, from, to, page = 0, size = 20 } = options

  return useQuery({
    queryKey: ['publications', teamSlug, { search, from, to, page, size }],
    queryFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(
        publicationsApi.listPublications(teamSlug, from, page, search, size, to)
      )
    },
    enabled: !!teamSlug,
    placeholderData: keepPreviousData,
  })
}
