import { z } from 'zod'
import { COMMON_ALIAS, pageField, sizeField } from './common'

export const TEAM_MEMBER_PAGE_SIZE = 50

export const teamMemberFiltersSchema = z.object({
  page: pageField,
  size: sizeField(TEAM_MEMBER_PAGE_SIZE),
})

export const teamMemberFiltersAlias = COMMON_ALIAS
