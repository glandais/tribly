import type { Instant } from './instant.ts'
import type { ReportReason } from './reportReason.ts'
import type { ReportTargetType } from './reportTargetType.ts'

/**
 * The reports tile of a team dashboard. The full queue is GET /api/teams/{teamSlug}/reports?status=OPEN; reporters are never named.
 */
export interface TeamDashboardReportsDto {
  /** Reported targets waiting for a decision — the number of items of the open queue */
  openCount: number
  /** Reason of the most recent open report, null when none */
  latestReason?: ReportReason
  /** What the most recent open report is about, null when none */
  latestTargetType?: ReportTargetType
  /** The reported text as it was when the most recent open report was filed, null when none */
  latestExcerpt?: string
  /** When the most recent open report was filed, null when none */
  latestReportedAt?: Instant
}
