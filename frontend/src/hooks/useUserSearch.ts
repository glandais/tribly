import { useQuery } from '@tanstack/react-query'
import { usersApi, unwrapResponse } from '../lib/apiClient'
import type { PublicUserDto } from '../api/api'

// Re-export type for convenience
export type { PublicUserDto }

export function useUserSearch(query: string, enabled: boolean = true) {
  return useQuery({
    queryKey: ['users', 'search', query],
    queryFn: async () => {
      if (!query || query.trim().length < 2) {
        return []
      }
      return await unwrapResponse(usersApi.searchUsers(undefined, query))
    },
    enabled: enabled && query.trim().length >= 2,
    staleTime: 30000, // 30 seconds
  })
}
