import * as zod from 'zod'

/**
 * Get frontend configuration including auth and app settings
 * @summary Get application configuration
 */
export const GetConfigResponse = zod
  .object({
    webAuthnRpId: zod.string().describe('WebAuthn Relying Party ID (effective host)'),
    appName: zod.string().describe('Application name'),
    singleTeam: zod.boolean().describe('Single team mode - team creation disabled'),
    pinnedTeamSlug: zod
      .string()
      .optional()
      .describe(
        'Slug of the team the site is pinned to (dedicated hostname \/ alias). Null on a regular multi-team domain. When set, the app roots on this team.'
      ),
  })
  .describe('Application configuration')
