import { ListViewMode } from '@/api/dto'

/**
 * Params of the two `size: 1` count queries behind the totals of « Mes sorties »'s two tabs, shared
 * by the page (`pages/profile/myRidesData.ts`) and its prefetch so both produce the same key.
 */
export const PARTICIPATION_COUNT_PARAMS = { size: 1, view: ListViewMode.COMPACT } as const
