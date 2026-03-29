import * as zod from 'zod'

/**
 * Get frontend configuration including auth and app settings
 * @summary Get application configuration
 */
export const GetConfigResponse = zod
  .object({
    webAuthnRpId: zod.string().describe('WebAuthn Relying Party ID (domain)'),
    appName: zod.string().describe('Application name'),
    singleTeam: zod.boolean().describe('Single team mode - team creation disabled'),
  })
  .describe('Application configuration')
