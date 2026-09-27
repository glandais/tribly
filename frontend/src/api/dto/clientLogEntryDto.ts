import type { ClientLogLevel } from './clientLogLevel.ts'
import type { Instant } from './instant.ts'

/**
 * One entry of the client's recent log
 */
export interface ClientLogEntryDto {
  /** When it was logged */
  ts: Instant
  /** Severity */
  level: ClientLogLevel
  /**
   * What logged it: console, http, navigation, error…
   * @maxLength 50
   * @pattern \S
   */
  source: string
  /**
   * The entry, truncated by the client
   * @maxLength 1000
   */
  message: string
}
