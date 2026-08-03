import { z } from 'zod'
import { COMMON_ALIAS, pageField, searchField, sizeField } from './common'
import {
  MEMBERSHIP_ALIAS,
  MEMBERSHIP_ALWAYS_SERIALIZE,
  membershipField,
  membershipToMinRole,
  type MembershipFilterValue,
} from './membership'

export const TEAM_PAGE_SIZE = 12

export const makeTeamFiltersSchema = (defaultMembership: MembershipFilterValue) =>
  z.object({
    search: searchField,
    membership: membershipField(defaultMembership),
    page: pageField,
    size: sizeField(TEAM_PAGE_SIZE),
  })

export const teamFiltersAlias = { ...COMMON_ALIAS, ...MEMBERSHIP_ALIAS } as const

export const teamFiltersAlwaysSerialize = MEMBERSHIP_ALWAYS_SERIALIZE

export type TeamFilters = z.infer<ReturnType<typeof makeTeamFiltersSchema>>

/**
 * Projects the team list's filters onto the endpoint's params — `membership` is the page's own
 * value, the API wants a MinRole. Shared with the `teams` route's `prefetch`.
 */
export function teamApiParams(filters: TeamFilters) {
  return {
    search: filters.search,
    page: filters.page,
    size: filters.size,
    minRole: membershipToMinRole[filters.membership],
  }
}
