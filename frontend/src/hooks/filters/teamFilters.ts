import { z } from 'zod'
import { COMMON_ALIAS, pageField, searchField, sizeField } from './common'
import {
  MEMBERSHIP_ALIAS,
  MEMBERSHIP_ALWAYS_SERIALIZE,
  membershipField,
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
