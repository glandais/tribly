import { useCallback, useMemo } from 'react'
import { keepPreviousData } from '@tanstack/react-query'
import type { QueryClient } from '@tanstack/react-query'
import { z } from 'zod'
import {
  getListMyParticipationsQueryKey,
  listMyParticipations,
  prefetchListMyParticipationsQuery,
  useListMyParticipations,
} from '@/api/endpoints/users/users'
import { ListViewMode } from '@/api/dto'
import type { ListMyParticipationsParams } from '@/api/dto'
import { prefetchPageWindow } from '@/config/prefetchHelpers'
import { COMMON_ALIAS, pageField, sizeField } from '@/hooks/filters/common'
import { useAuth } from '@/hooks/useAuth'
import { useMyParticipations } from '@/hooks/useMyParticipations'
import { usePaginatedQuery } from '@/hooks/usePaginatedQuery'
import { readUrlFilters, useUrlFilters } from '@/hooks/useUrlFilters'
import { PARTICIPATION_COUNT_PARAMS } from '@/components/profile/participationCountParams'
import { useAuthStore } from '@/store/authStore'
import { hourAlignedNowIso } from '@/utils/nowIso'

export const MY_RIDES_PAGE_SIZE = 12

export const MY_RIDES_TABS = ['upcoming', 'history'] as const
export type MyRidesTab = (typeof MY_RIDES_TABS)[number]

/**
 * « Mes sorties » (`/profil/sorties`): the tab and the page live in the query string, like every
 * list page's — a shared link or a back-navigation lands on the same tab and page.
 */
const myRidesFiltersSchema = z.object({
  tab: z.enum(MY_RIDES_TABS).default('upcoming').catch('upcoming'),
  page: pageField,
  size: sizeField(MY_RIDES_PAGE_SIZE),
})

export const myRidesFilterOptions = {
  schema: myRidesFiltersSchema,
  alias: { ...COMMON_ALIAS, tab: 'vue' },
} as const

type MyRidesFilters = z.infer<typeof myRidesFiltersSchema>

/**
 * The date window of a tab. `now` is `hourAlignedNowIso()` on both sides: the server's prefetch and
 * the client's first render must produce the same key, which a raw `new Date()` never does.
 */
function dateWindow(tab: MyRidesTab, now: string): Pick<ListMyParticipationsParams, 'from' | 'to'> {
  return tab === 'upcoming' ? { from: now } : { to: now }
}

function listParams(
  filters: MyRidesFilters,
  now: string
): ListMyParticipationsParams & { page: number } {
  return {
    ...dateWindow(filters.tab, now),
    page: filters.page,
    size: filters.size,
    view: ListViewMode.COMPACT,
  }
}

/** The two tabs' totals (`size: 1` queries, the same as the old accordion's) and the open tab's page. */
export function useMyRidesData() {
  const { isAuthenticated } = useAuth()
  const { filters, setFilters } = useUrlFilters(myRidesFilterOptions)
  // Frozen at mount so the two windows stay complementary while the page is open.
  const now = useMemo(() => hourAlignedNowIso(), [])

  const upcomingCount = useMyParticipations({ from: now, ...PARTICIPATION_COUNT_PARAMS })
  const historyCount = useMyParticipations({ to: now, ...PARTICIPATION_COUNT_PARAMS })

  const apiParams = useMemo(() => listParams(filters, now), [filters, now])
  // The previous page stays on screen while the next one loads, rather than a skeleton.
  const list = useListMyParticipations(apiParams, {
    query: { enabled: isAuthenticated, placeholderData: keepPreviousData },
  })

  const prefetchPage = useCallback(
    (page: number) => ({
      queryKey: getListMyParticipationsQueryKey({ ...apiParams, page }),
      queryFn: () => listMyParticipations({ ...apiParams, page }),
    }),
    [apiParams]
  )

  const { totalPages } = usePaginatedQuery({
    page: filters.page,
    pageSize: filters.size,
    totalItems: list.data?.total ?? 0,
    prefetchPage,
  })

  return { filters, setFilters, upcomingCount, historyCount, list, totalPages }
}

/** Server-side counterpart of {@link useMyRidesData}: both totals, and the tab's page window. */
export async function prefetchMyRides(queryClient: QueryClient, url: URL): Promise<void> {
  if (!useAuthStore.getState().isAuthenticated) return
  const now = hourAlignedNowIso()
  const filters = readUrlFilters(url.searchParams, myRidesFilterOptions)
  await Promise.all([
    prefetchListMyParticipationsQuery(queryClient, { from: now, ...PARTICIPATION_COUNT_PARAMS }),
    prefetchListMyParticipationsQuery(queryClient, { to: now, ...PARTICIPATION_COUNT_PARAMS }),
    prefetchPageWindow(listParams(filters, now), (p) =>
      prefetchListMyParticipationsQuery(queryClient, p)
    ),
  ])
}
