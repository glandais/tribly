import type { ClientContextDto } from './clientContextDto.ts'
import type { ClientErrorDto } from './clientErrorDto.ts'
import type { ClientLogEntryDto } from './clientLogEntryDto.ts'

/**
 * An unhandled error, reported automatically by a client
 */
export interface ErrorReportRequest {
  /** Client, device and screen */
  context: ClientContextDto
  /** The error */
  error: ClientErrorDto
  /**
   * The client's recent log, oldest first
   * @maxItems 50
   */
  logs?: ClientLogEntryDto[]
}
