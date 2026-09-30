export type EmailLinkKind = (typeof EmailLinkKind)[keyof typeof EmailLinkKind]

export const EmailLinkKind = {
  SIGN_UP: 'SIGN_UP',
  EMAIL_CHANGE: 'EMAIL_CHANGE',
} as const
