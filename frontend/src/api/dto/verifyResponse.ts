/**
 * User code verification response
 */
export interface VerifyResponse {
  /** User code */
  userCode: string
  /** Whether authorization is already completed */
  authorized?: boolean
}
