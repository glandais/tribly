import { z } from 'zod'
import { pageField, sizeField } from './common'

export const GPX_PREVIEW_PAGE_SIZE = 12

/** Keys match `ListMyPreviewsParams`; only pagination, no filters yet. */
export const gpxPreviewFiltersSchema = z.object({
  page: pageField,
  size: sizeField(GPX_PREVIEW_PAGE_SIZE),
})

export type GpxPreviewFilters = z.infer<typeof gpxPreviewFiltersSchema>

export const gpxPreviewFiltersAlias = { page: 'p' } as const
