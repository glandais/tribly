import { useQuery, keepPreviousData } from '@tanstack/react-query'
import { publicationsApi, unwrapResponse } from '../lib/apiClient'
import type { PublicationListResponse } from '../api/api'

// Re-export types for convenience
export type { PublicationListResponse }

interface UseAllPublicationsOptions {
  search?: string
  from?: string
  to?: string
  page?: number
  size?: number
}

export function useAllPublications(options: UseAllPublicationsOptions = {}) {
  const { search, from, to, page = 0, size = 20 } = options

  return useQuery({
    queryKey: ['publications', 'all', { search, from, to, page, size }],
    queryFn: async () => {
      return await unwrapResponse(publicationsApi.listAllPublications(from, page, search, size, to))
    },
    placeholderData: keepPreviousData,
  })
}
