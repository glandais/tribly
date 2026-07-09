/**
 * Request to create a domain alias (dedicated hostname pinned to a team)
 */
export interface CreateDomainAliasRequest {
  /**
   * Alias hostname
   * @maxLength 250
   * @pattern \S
   */
  hostname: string
  /**
   * Slug of the team to pin (must belong to the domain)
   * @pattern \S
   */
  teamSlug: string
  /**
   * Alias display name
   * @maxLength 250
   * @pattern \S
   */
  name: string
  /**
   * Base URL for the alias
   * @maxLength 500
   * @pattern \S
   */
  baseUrl: string
  /**
   * Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
   * @maxLength 1000
   */
  androidFingerprints?: string
}
