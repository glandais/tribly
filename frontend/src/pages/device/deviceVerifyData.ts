import type { QueryClient } from '@tanstack/react-query'
import { prefetchGetAvailableServicesQuery } from '@/api/endpoints/gps-services/gps-services'
import { useAuthStore } from '@/store/authStore'

/**
 * `DeviceVerifyPage` serves both `device-verify-garmin` and `device-verify-karoo` — same
 * component, the only difference (`isKarooPage`) is read from `location.pathname` at render time,
 * nothing server-derivable. The page itself owns no query directly, but it renders
 * `useGpsConnections()` unconditionally, which calls `useGetAvailableServices({ query: { enabled:
 * isAuthenticated } })` — the same gate `route-detail` already prefetches behind in
 * `pages/route/routeDetailData.ts`. Both routes are `auth: 'authenticated'`, so a signed-out
 * visitor never reaches this page server-side, but the gate is kept explicit rather than assumed,
 * matching the reference pattern.
 *
 * Prefetch-only, like `pages/auth/profileData.ts`: there is no `use<Screen>Data` because the page
 * consumes `useGpsConnections` directly, not a screen-specific hook.
 */
export async function prefetchDeviceVerify(queryClient: QueryClient): Promise<void> {
  if (useAuthStore.getState().isAuthenticated) {
    await prefetchGetAvailableServicesQuery(queryClient)
  }
}
