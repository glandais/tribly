/**
 * Exchange a one-time Strava login code for a session
 */
export interface StravaSessionRequest {
  /**
   * One-time login code from the Strava callback
   * @pattern \S
   */
  code: string
}
