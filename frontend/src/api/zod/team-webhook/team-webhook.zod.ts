import * as zod from 'zod'

/**
 * The message format is read from the URL: Slack, Discord, or a structured JSON document for anything else. Only https URLs to public addresses are accepted. Omitting the URL keeps the current one.
 * @summary Create or change the team's webhook
 */
export const SaveTeamWebhookParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const saveTeamWebhookBodyUrlMax = 1000

export const saveTeamWebhookBodyLanguageRegExp = new RegExp('fr|en')

export const SaveTeamWebhookBody = zod
  .object({
    url: zod
      .string()
      .max(saveTeamWebhookBodyUrlMax)
      .optional()
      .describe(
        'The https URL to post to. Omit it to keep the one already set — the API never returns it in full. Required when the team has no webhook yet.'
      ),
    language: zod
      .string()
      .regex(saveTeamWebhookBodyLanguageRegExp)
      .describe('Language the messages are written in'),
    enabled: zod.boolean().describe('Whether announcements are posted'),
  })
  .describe("Create or change a team's webhook")

export const SaveTeamWebhookResponse = zod
  .object({
    configured: zod.boolean().describe('Whether the team has a webhook at all'),
    maskedUrl: zod
      .string()
      .optional()
      .describe('The URL, masked: scheme, host and the last characters only'),
    kind: zod
      .enum(['SLACK', 'DISCORD', 'MATTERMOST', 'GENERIC'])
      .optional()
      .describe('Message format, read from the URL'),
    language: zod.string().optional().describe('Language the messages are written in'),
    enabled: zod.boolean().describe('Whether announcements are posted'),
    lastStatus: zod
      .enum(['PENDING', 'SENDING', 'SENT', 'SKIPPED', 'FAILED'])
      .optional()
      .describe('Outcome of the latest attempt'),
    lastError: zod.string().optional().describe('Why the latest attempt failed, when it did'),
    lastAttemptAt: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe('When the latest attempt was made'),
  })
  .describe("The team's outgoing webhook")

/**
 * The webhook with its URL masked — the URL is a secret. `configured` is false when the team has none.
 * @summary Get the team's webhook
 */
export const GetTeamWebhookParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const GetTeamWebhookResponse = zod
  .object({
    configured: zod.boolean().describe('Whether the team has a webhook at all'),
    maskedUrl: zod
      .string()
      .optional()
      .describe('The URL, masked: scheme, host and the last characters only'),
    kind: zod
      .enum(['SLACK', 'DISCORD', 'MATTERMOST', 'GENERIC'])
      .optional()
      .describe('Message format, read from the URL'),
    language: zod.string().optional().describe('Language the messages are written in'),
    enabled: zod.boolean().describe('Whether announcements are posted'),
    lastStatus: zod
      .enum(['PENDING', 'SENDING', 'SENT', 'SKIPPED', 'FAILED'])
      .optional()
      .describe('Outcome of the latest attempt'),
    lastError: zod.string().optional().describe('Why the latest attempt failed, when it did'),
    lastAttemptAt: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe('When the latest attempt was made'),
  })
  .describe("The team's outgoing webhook")

/**
 * @summary Remove the team's webhook
 */
export const DeleteTeamWebhookParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const DeleteTeamWebhookResponse = zod.void()

/**
 * Posts a test message now and reports how the endpoint answered.
 * @summary Send a test message to the team's webhook
 */
export const TestTeamWebhookParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const TestTeamWebhookResponse = zod
  .object({
    success: zod.boolean().describe('Whether the endpoint accepted it (2xx)'),
    statusCode: zod.int().optional().describe('HTTP status the endpoint answered, if it answered'),
    error: zod.string().optional().describe('Why it failed, when it did'),
  })
  .describe("Outcome of a test message sent to the team's webhook")
