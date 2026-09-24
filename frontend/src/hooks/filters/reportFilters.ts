import { z } from 'zod'
import { ReportQueueStatus } from '@/api/dto'

/**
 * A moderation queue's one filter: open reports, or the latest decisions. In the URL so a moderator
 * coming back from the reported content lands on the same tab.
 */
export const reportFiltersSchema = z.object({
  status: z.enum(ReportQueueStatus).default(ReportQueueStatus.OPEN).catch(ReportQueueStatus.OPEN),
})

export type ReportFilters = z.infer<typeof reportFiltersSchema>

export const reportFiltersAlias = {} as const
