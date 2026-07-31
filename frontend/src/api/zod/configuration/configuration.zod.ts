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
    enableGpxPlanner: zod
      .boolean()
      .describe(
        'Whether the interactive planner is open in the team-independent GPX tools. When false the tools still accept a .gpx upload, only drawing from scratch is closed. Platform-admin only, per domain.'
      ),
    pinnedTeamSlug: zod
      .string()
      .optional()
      .describe(
        'Slug of the team the site is pinned to (dedicated hostname \/ alias). Null on a regular multi-team domain. When set, the app roots on this team.'
      ),
    mapStyles: zod
      .array(
        zod
          .object({
            id: zod.string().describe("Stable style identifier, e.g. 'colorful'"),
            label: zod.string().describe('Human-readable label for the style switcher'),
            group: zod
              .string()
              .describe(
                "Section the switcher should list this style under — 'vector', 'satellite' or 'raster'. Clients group consecutive styles sharing a value and localise the heading themselves; an unknown value is rendered as its own section rather than dropped."
              ),
            url: zod
              .string()
              .describe(
                'URL of the MapLibre style document to load in light mode (or at all times when darkVariant is null). Either a third-party style document or, for a raster basemap the server wraps itself, a URL on \/api\/map\/styles\/{id}.json.'
              ),
            darkVariant: zod
              .string()
              .optional()
              .describe(
                "URL of the style document to load instead of 'url' when the client renders in dark mode. Null when the style has no dark counterpart — the client then keeps using 'url'."
              ),
          })
          .describe('A basemap style the clients may offer')
      )
      .describe(
        'Basemaps the clients may offer, in switcher order. Served rather than compiled in, so a tile provider can change without a client release.'
      ),
    tileServerBaseUrl: zod
      .string()
      .describe(
        'Public base URL of the tile host, for a client that builds its own style, sprite or glyph URLs.'
      ),
    defaultCenter: zod
      .object({
        lat: zod.number().describe('Latitude in WGS84 degrees'),
        lon: zod.number().describe('Longitude in WGS84 degrees'),
        zoom: zod.number().describe('Initial zoom level'),
      })
      .describe(
        "Where a map opens before it knows what it is showing. On a site rooted on one team this is that team's location; otherwise the deployment default."
      ),
    minSupportedAppVersion: zod
      .string()
      .optional()
      .describe(
        'Oldest mobile build this server still serves, as a semver string. Null when no floor is enforced; a client older than this should tell the user to update.'
      ),
  })
  .describe('Application configuration')

/**
 * Returns the MapLibre style document for a raster basemap whose provider serves tiles only. The URL is the one already carried by MapStyleDto.url — clients hand it to the map engine rather than building it themselves.
 * @summary Get a generated map style
 */
export const GetStyleParams = zod.object({
  id: zod.string(),
})

export const GetStyleResponse = zod
  .looseObject({})
  .describe('A MapLibre GL style specification document')
