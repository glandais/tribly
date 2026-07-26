import type { MapCenterDto } from './mapCenterDto.ts'
import type { MapStyleDto } from './mapStyleDto.ts'

/**
 * Application configuration
 */
export interface ConfigDto {
  /** WebAuthn Relying Party ID (effective host) */
  webAuthnRpId: string
  /** Application name */
  appName: string
  /** Single team mode - team creation disabled */
  singleTeam: boolean
  /** Slug of the team the site is pinned to (dedicated hostname / alias). Null on a regular multi-team domain. When set, the app roots on this team. */
  pinnedTeamSlug?: string
  /** Basemaps the clients may offer, in switcher order. Served rather than compiled in, so a tile provider can change without a client release. */
  mapStyles: MapStyleDto[]
  /** Public base URL of the tile host, for a client that builds its own style, sprite or glyph URLs. */
  tileServerBaseUrl: string
  /** Where a map opens before it knows what it is showing. On a site rooted on one team this is that team's location; otherwise the deployment default. */
  defaultCenter: MapCenterDto
  /** Oldest mobile build this server still serves, as a semver string. Null when no floor is enforced; a client older than this should tell the user to update. */
  minSupportedAppVersion?: string
}
