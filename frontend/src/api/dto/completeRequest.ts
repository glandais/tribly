/**
 * Complete device authorization request
 */
export interface CompleteRequest {
  /** User code from device display */
  userCode: string
  /** Must be true: the user explicitly confirmed, on a screen showing the code */
  confirmed: boolean
}
