import type { Instant } from './instant.ts'

/**
 * User code verification response
 */
export interface VerifyResponse {
  /** User code */
  userCode: string
  /** Whether authorization is already completed */
  authorized?: boolean
  /** Which kind of device asks (e.g. 'karoo', 'garmin'), to name it on the confirmation screen */
  clientId: string
  /** When the device asked for the code: a code the user did not request themselves a moment ago stands out */
  requestedAt: Instant
}
