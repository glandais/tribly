import { z } from 'zod'
import { ListViewMode, PublicationType, PublicationWhen } from '@/api/dto'
import { COMMON_ALIAS, pageField, searchField, sizeField, tagIdsField } from './common'

export const AGENDA_PAGE_SIZE = 12

/** The kinds of the agenda: its rides and trips, never its posts (plan 2026-10-06 §2). */
export type AgendaTypeValue = 'all' | 'ride' | 'trip'

/**
 * The agenda's time scopes (plan §2.2): « À venir » (the default), « Je participe », « Passées ».
 * There is no « Tout »: sorted one way, it began with the farthest ride and ended in the archive.
 * `me` is signed-in only — `participating` yields nothing for an anonymous visitor.
 */
export type AgendaScopeValue = 'upcoming' | 'me' | 'past'

export const agendaTypeToPublicationType: Record<AgendaTypeValue, PublicationType | undefined> = {
  all: undefined,
  ride: PublicationType.RIDE,
  trip: PublicationType.TRIP,
}

/**
 * The agenda's filters, as its URL carries them. A former feed link keeps working: `?w=all`, a
 * scope the agenda no longer has, falls back to « À venir », and `?type=post` to « Tout ».
 */
export const agendaFiltersSchema = z.object({
  search: searchField,
  filter: z.enum(['all', 'ride', 'trip']).default('all').catch('all'),
  scope: z.enum(['upcoming', 'me', 'past']).default('upcoming').catch('upcoming'),
  tags: tagIdsField,
  /**
   * « Vignettes » or « Lignes » (`?view=`, ledger `WEB-69`). Presentation only: never sent to the
   * API. The calendar view has its own path (`teamCalendar`).
   */
  density: z.enum(['card', 'row']).optional().catch(undefined),
  page: pageField,
  size: sizeField(AGENDA_PAGE_SIZE),
})

export type AgendaFilters = z.infer<typeof agendaFiltersSchema>

export const agendaFiltersAlias = {
  ...COMMON_ALIAS,
  filter: 'type',
  scope: 'w',
  density: 'view',
} as const

/**
 * Projects the agenda's filters onto `GET /api/teams/{slug}/publications` — the one place the page
 * and its route `prefetch` both go through, so their query keys match byte for byte.
 *
 * The scope is `when`, and the server sorts accordingly (API-85): `UPCOMING` keeps what is not
 * over yet — a ride under way included — soonest first, `PAST` what is over, latest first. No
 * `from=now` any more, hence no clock in the key. « Je participe » is `UPCOMING` plus
 * `participating`. Tags only go with a kind: each kind has its own vocabulary (plan D3), and the
 * API ignores them without `type`.
 */
export function agendaApiParams(filters: AgendaFilters) {
  const type = agendaTypeToPublicationType[filters.filter]
  return {
    search: filters.search,
    page: filters.page,
    size: filters.size,
    type,
    when: filters.scope === 'past' ? PublicationWhen.PAST : PublicationWhen.UPCOMING,
    ...(filters.scope === 'me' ? { participating: true } : {}),
    ...(type && filters.tags ? { tags: filters.tags } : {}),
    view: ListViewMode.COMPACT,
  }
}
