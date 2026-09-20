import { useCallback, useMemo } from 'react'
import { keepPreviousData } from '@tanstack/react-query'
import {
  getListMyNotificationsQueryKey,
  listMyNotifications,
  useListMyNotifications,
} from '@/api/endpoints/notifications/notifications'
import { usePaginatedQuery } from '@/hooks/usePaginatedQuery'
import { useUrlFilters } from '@/hooks/useUrlFilters'
import {
  notificationFiltersAlias,
  notificationFiltersSchema,
} from '@/hooks/filters/notificationFilters'

/**
 * What the notifications page reads: the filtered page the URL asks for, plus the neighbouring
 * pages `usePaginatedQuery` warms.
 *
 * No `prefetch` counterpart, unlike the other list pages: the inbox is per-user and the route is
 * `authenticated`, so there is nothing an anonymous server render could usefully produce. It keeps
 * its own module all the same, so adding one later doesn't mean moving the query out of the page.
 */
export const notificationListFilterOptions = {
  schema: notificationFiltersSchema,
  alias: notificationFiltersAlias,
} as const

export function useNotificationListData() {
  const { filters, setFilters } = useUrlFilters(notificationListFilterOptions)

  const apiParams = useMemo(
    () => ({ page: filters.page, size: filters.size, unreadOnly: filters.unreadOnly }),
    [filters.page, filters.size, filters.unreadOnly]
  )

  const notifications = useListMyNotifications(apiParams, {
    query: { placeholderData: keepPreviousData },
  })

  const prefetchPage = useCallback(
    (page: number) => ({
      queryKey: getListMyNotificationsQueryKey({ ...apiParams, page }),
      queryFn: () => listMyNotifications({ ...apiParams, page }),
    }),
    [apiParams]
  )

  const { totalPages } = usePaginatedQuery({
    page: filters.page,
    pageSize: filters.size,
    totalItems: notifications.data?.total ?? 0,
    prefetchPage,
  })

  return { filters, setFilters, notifications, totalPages }
}
