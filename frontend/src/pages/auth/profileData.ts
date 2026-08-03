import type { QueryClient } from '@tanstack/react-query'
import {
  prefetchListMyParticipationsQuery,
  prefetchGetLatestExportQuery,
} from '@/api/endpoints/users/users'
import { prefetchListPasskeysQuery } from '@/api/endpoints/passkeys/passkeys'
import { prefetchGetAvailableServicesQuery } from '@/api/endpoints/gps-services/gps-services'
import { PARTICIPATION_COUNT_PARAMS } from '@/components/profile/participationCountParams'
import { useAuthStore } from '@/store/authStore'
import { hourAlignedNowIso } from '@/utils/nowIso'

/**
 * Server-side counterpart of everything `UserProfilePage` puts on the screen at first paint.
 * There is no `useProfileData` hook here: the page itself owns no query, it just mounts children
 * that do, so this module stays prefetch-only, same shape as `rideDetailData.ts`'s sibling.
 *
 * - The two `size: 1` count queries `MyParticipations` puts on its closed accordion controls
 *   (`useMyParticipations({ from: now, ...PARTICIPATION_COUNT_PARAMS })` and its `{ to: now, ... }`
 *   twin), keyed on the *same* `hourAlignedNowIso()` the component itself calls — the shared hour
 *   boundary is the whole reason these are prefetchable at all: a raw `new Date()` on either side
 *   would differ by milliseconds and never hit the same cache entry. `PARTICIPATION_COUNT_PARAMS`
 *   keeps living in `components/profile/participationCountParams.ts` (its own module for the same
 *   reason this one exists: `routes.config.ts` is imported eagerly and must not pull
 *   `MyParticipations`, or the page, out of their lazy chunks).
 * - `PasskeyManager`'s `useListPasskeys()` — unconditional, no param.
 * - `GpsConnectionsManager` (via `useGpsConnections`)'s `useGetAvailableServices()` — unconditional
 *   on the hook's own `enabled: isAuthenticated`, no param.
 * - `DataExportManager` (via `useDataExport`)'s `useGetLatestExport()` — unconditional on the same
 *   `isAuthenticated` gate, no param; only its *polling* while an export is in flight is a client
 *   concern.
 *
 * All three render on first paint — none behind a tab, modal or accordion — so unlike the
 * participation counts they need no shared derivation, just the matching generated `prefetchXxxQuery`.
 *
 * The section's paged participation queries are deliberately NOT prefetched here — they only fire
 * once the accordion item opens, exactly as `ParticipationsPanel` gates them on `opened`.
 */
export async function prefetchUserProfile(queryClient: QueryClient): Promise<void> {
  if (!useAuthStore.getState().isAuthenticated) return
  const now = hourAlignedNowIso()
  await Promise.all([
    prefetchListMyParticipationsQuery(queryClient, { from: now, ...PARTICIPATION_COUNT_PARAMS }),
    prefetchListMyParticipationsQuery(queryClient, { to: now, ...PARTICIPATION_COUNT_PARAMS }),
    prefetchListPasskeysQuery(queryClient),
    prefetchGetAvailableServicesQuery(queryClient),
    prefetchGetLatestExportQuery(queryClient),
  ])
}
