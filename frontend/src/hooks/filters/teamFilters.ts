import { z } from 'zod'
import { MinRole } from '@/api/dto'
import { COMMON_ALIAS, pageField, searchField, sizeField } from './common'

export const TEAM_PAGE_SIZE = 12

export type TeamFilterValue = 'all' | 'member' | 'organizer' | 'admin'

export const teamFilterToMinRole: Record<TeamFilterValue, MinRole | undefined> = {
  all: undefined,
  member: MinRole.MEMBER,
  organizer: MinRole.ORGANIZER,
  admin: MinRole.ADMIN,
}

/** The default depends on authentication, so the schema is built per render. */
export const makeTeamFiltersSchema = (defaultFilter: TeamFilterValue) =>
  z.object({
    search: searchField,
    filter: z
      .enum(['all', 'member', 'organizer', 'admin'])
      .default(defaultFilter)
      .catch(defaultFilter),
    page: pageField,
    size: sizeField(TEAM_PAGE_SIZE),
  })

export const teamFiltersAlias = { ...COMMON_ALIAS, filter: 'role' } as const

/**
 * `filter` is always written out: its default differs for signed-in and
 * anonymous visitors, so omitting it would make a shared link resolve
 * differently for the recipient.
 */
export const teamFiltersAlwaysSerialize = ['filter'] as const
