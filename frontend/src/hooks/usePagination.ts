import { useState, useMemo, useCallback } from 'react'

export interface UsePaginationOptions {
  initialPage?: number
  pageSize: number
  totalItems: number
}

export interface UsePaginationResult {
  page: number
  setPage: (page: number) => void
  pageSize: number
  totalPages: number
  reset: () => void
}

export function usePagination({
  initialPage = 0,
  pageSize,
  totalItems,
}: UsePaginationOptions): UsePaginationResult {
  const [page, setPage] = useState(initialPage)

  const totalPages = useMemo(() => {
    return Math.ceil(totalItems / pageSize)
  }, [totalItems, pageSize])

  const reset = useCallback(() => {
    setPage(0)
  }, [])

  return {
    page,
    setPage,
    pageSize,
    totalPages,
    reset,
  }
}
