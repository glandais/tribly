/**
 * A request to be notified and added to a beta program once one opens
 */
export interface BetaSignupRequest {
  /**
   * Email to contact once a beta is open
   * @pattern \S
   */
  email: string
}
