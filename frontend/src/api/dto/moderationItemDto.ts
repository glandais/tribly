import type { Instant } from './instant.ts'
import type { PublicUserDto } from './publicUserDto.ts'
import type { ReportReason } from './reportReason.ts'
import type { ReportStatus } from './reportStatus.ts'
import type { ReportTargetType } from './reportTargetType.ts'

/**
 * One reported target in a moderation queue, with all its reports grouped
 */
export interface ModerationItemDto {
  /** Type of the reported target */
  targetType: ReportTargetType
  /** ID (TSID) of the reported target */
  targetId: string
  /** Slug of the team the reports were filed in */
  teamSlug: string
  /** Name of that team */
  teamName: string
  /** Who the moderation is about: the author of the content, or the member */
  targetUser: PublicUserDto
  /** Name of the publication — of the commented one for a comment. Null for a member, or when the content is gone. */
  contentName?: string
  /** Type of the content to open: the publication itself, or the one a comment is on (POST, RIDE, TRIP, ROUTE or AD). Null for a member, or when the content is gone. */
  contentType?: ReportTargetType
  /** Slug of the content to open, with contentType */
  contentSlug?: string
  /** The reported text as it was when first reported (comment text, name and start of the description, or member name) */
  excerpt?: string
  /** How many reports this target gathered */
  reportCount: number
  /** The distinct reasons given */
  reasons: ReportReason[]
  /** The non-empty free texts of the reports */
  messages: string[]
  /** When the first report was filed */
  firstReportedAt: Instant
  /** When the last report was filed */
  lastReportedAt: Instant
  /** Whether the content is currently hidden from members, having gathered enough reports */
  hidden: boolean
  /** OPEN while waiting; REMOVED or DISMISSED once decided (the latest decision) */
  status: ReportStatus
  /** Who reported. Only in the platform queue: always null in a team's queue, where reporters stay anonymous. */
  reporters?: PublicUserDto[]
}
