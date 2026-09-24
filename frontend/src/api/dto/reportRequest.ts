import type { ReportReason } from './reportReason.ts'
import type { ReportTargetType } from './reportTargetType.ts'

/**
 * A report of a comment, a publication, an ad, a route or a member
 */
export interface ReportRequest {
  /**
   * The team the target belongs to. Its organizers and administrators moderate the report.
   * @pattern \S
   */
  teamSlug: string
  /** What is reported */
  targetType: ReportTargetType
  /**
   * ID (TSID) of the comment, publication, ad or route — or of the user for a MEMBER
   * @pattern \S
   */
  targetId: string
  /** Why */
  reason: ReportReason
  /**
   * Optional free text for the moderators, up to 500 characters
   * @maxLength 500
   */
  message?: string
}
