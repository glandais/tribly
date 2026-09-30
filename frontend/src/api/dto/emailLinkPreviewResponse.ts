import type { EmailLinkKind } from './emailLinkKind.ts'

/**
 * What a verification link is about, read without spending it: the page shows the address before anything happens, so that nobody activates someone else's account unaware.
 */
export interface EmailLinkPreviewResponse {
  /** The address the link verifies */
  email: string
  /** What following the link does */
  kind: EmailLinkKind
}
