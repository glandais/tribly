/**
 * Complete device authorization request
 */
export interface CompleteRequest {
  /** User code from device display */
  userCode: string
  /** Authenticated user ID (TSID string) */
  userId: string
}
