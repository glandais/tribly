import { useEffect, useMemo } from 'react'
import { useQueryClient, QueryKey, QueryFunction } from '@tanstack/react-query'

export interface UsePaginatedQueryOptions {
  page: number
  pageSize: number
  totalItems: number
  prefetchPage?: (page: number) => { queryKey: QueryKey; queryFn: QueryFunction }
}

export interface UsePaginatedQueryResult {
  totalPages: number
}

export function usePaginatedQuery({
  page,
  pageSize,
  totalItems,
  prefetchPage,
}: UsePaginatedQueryOptions): UsePaginatedQueryResult {
  const queryClient = useQueryClient()

  const totalPages = useMemo(() => Math.ceil(totalItems / pageSize), [totalItems, pageSize])

  // Prefetch next page
  useEffect(() => {
    if (prefetchPage && page < totalPages - 1) {
      const { queryKey, queryFn } = prefetchPage(page + 1)
      queryClient.prefetchQuery({ queryKey, queryFn })
    }
  }, [page, totalPages, prefetchPage, queryClient])

  // Prefetch previous page
  useEffect(() => {
    if (prefetchPage && page > 0) {
      const { queryKey, queryFn } = prefetchPage(page - 1)
      queryClient.prefetchQuery({ queryKey, queryFn })
    }
  }, [page, prefetchPage, queryClient])

  return { totalPages }
}
