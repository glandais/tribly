import { useCallback } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import {
  getCountMyUnreadNotificationsQueryKey,
  getListMyNotificationsQueryKey,
  useCountMyUnreadNotifications,
  useMarkAllNotificationsRead,
  useMarkNotificationRead,
} from '@/api/endpoints/notifications/notifications'
import { useAuthStore, selectIsAuthenticated } from '@/store/authStore'

/**
 * How often the bell re-asks for the unread count. The plan has no realtime channel in phase 1
 * (`docs/plans/2026-09-18-notifications.md` §9): clients poll this cheap endpoint on focus and at
 * most once a minute. Shortening it buys very little and costs one request per minute per open tab.
 */
const UNREAD_POLL_INTERVAL_MS = 60_000

/** Everything that changes when a notification is read: the badge, and every page of the list. */
function useInvalidateNotifications() {
  const queryClient = useQueryClient()
  return useCallback(() => {
    void queryClient.invalidateQueries({ queryKey: getCountMyUnreadNotificationsQueryKey() })
    // Prefix match: every `?page=&unreadOnly=` variant of the list shares this first key segment.
    void queryClient.invalidateQueries({ queryKey: getListMyNotificationsQueryKey() })
  }, [queryClient])
}

/**
 * The unread badge. Only queried for a signed-in visitor — the endpoint 401s otherwise, and the
 * bell isn't rendered anyway.
 */
export function useUnreadNotificationCount() {
  const isAuthenticated = useAuthStore(selectIsAuthenticated)

  const { data } = useCountMyUnreadNotifications({
    query: {
      enabled: isAuthenticated,
      refetchOnWindowFocus: true,
      refetchInterval: UNREAD_POLL_INTERVAL_MS,
      staleTime: UNREAD_POLL_INTERVAL_MS,
    },
  })

  return data?.count ?? 0
}

/**
 * Marking read, from the bell or from the list. `markRead` is idempotent server-side, so an
 * already-read notification is not worth a round trip.
 */
export function useNotificationActions() {
  const invalidate = useInvalidateNotifications()
  const markReadMutation = useMarkNotificationRead({ mutation: { onSuccess: invalidate } })
  const markAllReadMutation = useMarkAllNotificationsRead({ mutation: { onSuccess: invalidate } })

  const markRead = useCallback(
    (id: string, alreadyRead: boolean) => {
      if (alreadyRead) return
      markReadMutation.mutate({ notificationId: id })
    },
    [markReadMutation]
  )

  return {
    markRead,
    markAllRead: () => markAllReadMutation.mutate(undefined),
    isMarkingAllRead: markAllReadMutation.isPending,
  }
}
