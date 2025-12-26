import { useQuery } from '@tanstack/react-query'
import { publicationsApi, unwrapResponse } from '../lib/apiClient'
import type { PublicationListResponse } from '../api/api'
import { Status } from '../api/api'

// Re-export types for convenience
export type { PublicationListResponse }

// Re-export enums as values (not types)
export { Status }

interface UsePublicationsOptions {
  from?: string
  to?: string
  status?: Status
  page?: number
  size?: number
}

export function usePublications(
  teamSlug: string | undefined,
  options: UsePublicationsOptions = {}
) {
  const { from, to, status, page = 0, size = 20 } = options

  return useQuery({
    queryKey: ['publications', teamSlug, { from, to, status, page, size }],
    queryFn: async () => {
      if (!teamSlug) throw new Error('Team slug is required')
      return await unwrapResponse(
        publicationsApi.listPublications(teamSlug, from, page, size, status, to)
      )
    },
    enabled: !!teamSlug,
    staleTime: 1000 * 60 * 2,
  })
}
