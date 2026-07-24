/**
 * Server API contract version and build information
 */
export interface VersionDto {
  /** API contract version (semver). Bumped whenever the OpenAPI contract changes, and also exposed as info.version in the contract itself. */
  apiVersion: string
  /** Short git commit the server was built from, null when unavailable */
  commit?: string
  /** Build timestamp (ISO-8601, UTC), null when unavailable */
  buildTime?: string
}
