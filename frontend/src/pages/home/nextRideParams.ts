import { ListViewMode, Status } from '@/api/dto'
import type { ListMyParticipationsParams } from '@/api/dto'

/**
 * The query behind "ma prochaine sortie" on the home feed: the visitor's next few published
 * participations, out of which the card picks the first ride they are registered to.
 *
 * `size: 5` and not 1 because the window may open on trips or on rides the visitor merely
 * watches; five is enough to find a registered one without paging.
 *
 * Shared with the `home` route's `prefetch` — its own module rather than an export of
 * `HomePage.tsx`, which `routes.config.ts` must not pull out of its lazy chunk. `from` stays a
 * parameter: it is `hourAlignedNowIso()`, computed on each side at render/prefetch time.
 */
export const NEXT_RIDE_PARAMS = {
  status: Status.PUBLISHED,
  size: 5,
  view: ListViewMode.COMPACT,
} as const satisfies Omit<ListMyParticipationsParams, 'from'>
