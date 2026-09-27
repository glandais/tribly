import type { ClientContextDto } from './clientContextDto.ts'
import type { ClientErrorDto } from './clientErrorDto.ts'
import type { ClientLogEntryDto } from './clientLogEntryDto.ts'
import type { FeedbackKind } from './feedbackKind.ts'

/**
 * A bug report or a suggestion written by a member
 */
export interface FeedbackRequest {
  /** Bug or suggestion */
  kind: FeedbackKind
  /**
   * What happened, in the member's words
   * @minLength 10
   * @maxLength 5000
   */
  message: string
  /** Client, device and screen */
  context: ClientContextDto
  /** The unhandled error the report was opened from, if any. Links the report to the automatic error report of the same error. */
  error?: ClientErrorDto
  /**
   * The client's recent log, oldest first. Absent when the member chose not to attach technical details.
   * @maxItems 200
   */
  logs?: ClientLogEntryDto[]
}
