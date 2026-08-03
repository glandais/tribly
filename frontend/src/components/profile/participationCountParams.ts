import { ListViewMode } from '@/api/dto'

/**
 * Params of the two `size: 1` count queries `MyParticipations` puts on its closed accordion
 * controls, shared with the `profile` route's `prefetch` so both produce the same query key.
 *
 * Its own module, not an export of `MyParticipations.tsx`: `routes.config.ts` is eagerly imported
 * by both entries, so importing the constant from the component would pull the component — and
 * everything it renders — out of its lazy chunk and into the main bundle.
 */
export const PARTICIPATION_COUNT_PARAMS = { size: 1, view: ListViewMode.COMPACT } as const
