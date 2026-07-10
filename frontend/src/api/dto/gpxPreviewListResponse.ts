import type { GpxPreviewSummaryDto } from './gpxPreviewSummaryDto.ts'

/**
 * Paginated GPX preview list response
 */
export interface GpxPreviewListResponse {
  /** List of GPX previews */
  previews: GpxPreviewSummaryDto[]
  /** Total number of previews */
  total: number
  /** Current page number */
  page: number
  /** Page size */
  size: number
}
