export type TeamWebhookKind = (typeof TeamWebhookKind)[keyof typeof TeamWebhookKind]

export const TeamWebhookKind = {
  SLACK: 'SLACK',
  DISCORD: 'DISCORD',
  MATTERMOST: 'MATTERMOST',
  GENERIC: 'GENERIC',
} as const
