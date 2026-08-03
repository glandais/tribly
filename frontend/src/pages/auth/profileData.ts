import type { QueryClient } from '@tanstack/react-query'
import { prefetchListMyParticipationsQuery } from '@/api/endpoints/users/users'
import { PARTICIPATION_COUNT_PARAMS } from '@/components/profile/participationCountParams'
import { useAuthStore } from '@/store/authStore'
import { hourAlignedNowIso } from '@/utils/nowIso'

/**
 * Server-side counterpart of the two `size: 1` count queries `MyParticipations` puts on its
 * closed accordion controls (`useMyParticipations({ from: now, ...PARTICIPATION_COUNT_PARAMS })`
 * and the `{ to: now, ... }` twin). There is no `useProfileData` hook here: `UserProfilePage`
 * itself owns none of this data — it just mounts `<MyParticipations />`, which reads the counts
 * directly via `useMyParticipations` and its own `hourAlignedNowIso()`. This module exists only so
 * the `profile` route's `prefetch` in `routes.config.ts` calls the *same* `hourAlignedNowIso()` —
 * the shared hour boundary is the whole reason these counts are prefetchable at all: a raw
 * `new Date()` on either side would differ by milliseconds from the other and never hit the same
 * cache entry.
 *
 * `PARTICIPATION_COUNT_PARAMS` keeps living in `components/profile/participationCountParams.ts`
 * (its own module for the same reason this one exists: `routes.config.ts` is imported eagerly and
 * must not pull `MyParticipations`, or the page, out of their lazy chunks).
 *
 * The section's paged queries are deliberately NOT prefetched here — they only fire once the
 * accordion item opens, exactly as `ParticipationsPanel` gates them on `opened`.
 */
export async function prefetchUserProfile(queryClient: QueryClient): Promise<void> {
  if (!useAuthStore.getState().isAuthenticated) return
  const now = hourAlignedNowIso()
  await Promise.all([
    prefetchListMyParticipationsQuery(queryClient, { from: now, ...PARTICIPATION_COUNT_PARAMS }),
    prefetchListMyParticipationsQuery(queryClient, { to: now, ...PARTICIPATION_COUNT_PARAMS }),
  ])
}
