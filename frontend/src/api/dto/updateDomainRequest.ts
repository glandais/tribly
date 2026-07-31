/**
 * Request to update a domain
 */
export interface UpdateDomainRequest {
  /**
   * Domain display name
   * @maxLength 250
   * @pattern \S
   */
  name: string
  /**
   * Base URL for the domain
   * @maxLength 500
   * @pattern \S
   */
  baseUrl: string
  /** Whether domain is single-team mode */
  singleTeam?: boolean
  /** Whether the interactive planner is open in the GPX tools */
  enableGpxPlanner?: boolean
  /**
   * Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
   * @maxLength 1000
   */
  androidFingerprints?: string
}
