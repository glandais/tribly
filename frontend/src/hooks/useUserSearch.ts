import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../api/client';

export interface UserSearchResult {
  id: string;
  displayName: string;
  avatarUrl: string | null;
}

export function useUserSearch(query: string, enabled: boolean = true) {
  return useQuery({
    queryKey: ['users', 'search', query],
    queryFn: async () => {
      if (!query || query.trim().length < 2) {
        return [];
      }
      return apiClient.get<UserSearchResult[]>(`/users/search?q=${encodeURIComponent(query)}`);
    },
    enabled: enabled && query.trim().length >= 2,
    staleTime: 30000, // 30 seconds
  });
}
