import { z } from 'zod'
import { AdType } from '@/api/dto'
import { COMMON_ALIAS, pageField, searchField, sizeField } from './common'

export const AD_PAGE_SIZE = 12

export const adFiltersSchema = z.object({
  search: searchField,
  adType: z.enum(AdType).optional().catch(undefined),
  page: pageField,
  size: sizeField(AD_PAGE_SIZE),
})

export const adFiltersAlias = { ...COMMON_ALIAS, adType: 'type' } as const
