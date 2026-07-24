import * as zod from 'zod'

/**
 * Get the API contract version the server implements, plus the git commit and build time it was built from
 * @summary Get server version
 */
export const GetVersionResponse = zod
  .object({
    apiVersion: zod
      .string()
      .describe(
        'API contract version (semver). Bumped whenever the OpenAPI contract changes, and also exposed as info.version in the contract itself.'
      ),
    commit: zod
      .string()
      .optional()
      .describe('Short git commit the server was built from, null when unavailable'),
    buildTime: zod
      .string()
      .optional()
      .describe('Build timestamp (ISO-8601, UTC), null when unavailable'),
  })
  .describe('Server API contract version and build information')
