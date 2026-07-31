/**
 * Redeem an invitation token
 */
export interface AcceptInvitationRequest {
  /**
   * The token from the invitation e-mail. Sent in the body rather than in the path, because a bearer secret in a URL path ends up in access logs and in the Referer of anything the page loads.
   * @maxLength 200
   * @pattern \S
   */
  token: string
}
