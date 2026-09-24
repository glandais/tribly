import type { ModerationAction } from './moderationAction.ts'
import type { ReportTargetType } from './reportTargetType.ts'

/**
 * A moderator's decision, applied to every open report of one target
 */
export interface ModerationDecisionRequest {
  /** Type of the reported target */
  targetType: ReportTargetType
  /**
   * ID (TSID) of the reported target
   * @pattern \S
   */
  targetId: string
  /** REMOVE_CONTENT deletes the content (not allowed on a MEMBER); DISMISS keeps it and shows it again if reports had hidden it */
  action: ModerationAction
}
