import type { QueryClient } from '@tanstack/react-query'
import { prefetchGetStatsQuery } from '@/api/endpoints/admin-domains/admin-domains'
import { useAuthStore } from '@/store/authStore'

/**
 * Server-side counterpart of `AdminDashboardPage`, which reads `useGetStats()` unconditionally
 * (no params, no gate in the hook itself). Prefetch-only — the page owns exactly this one query,
 * so a hook here would just re-export it for no reader.
 *
 * Gated on `isAuthenticated` only: the route (`admin`) is `auth: 'authenticated'` and the store
 * carries no platform-admin flag to check server-side. A signed-in non-admin still gets a
 * prefetch that resolves to a 403 from `GET /admin/stats` — one wasted request, same cost the
 * client would have paid after hydration, and cheaper than adding a second call just to find out.
 */
export async function prefetchAdminDashboard(queryClient: QueryClient): Promise<void> {
  if (!useAuthStore.getState().isAuthenticated) return
  await prefetchGetStatsQuery(queryClient)
}
