export const TERRAIN_URL = 'https://tiles.mapterhorn.com/tilejson.json'
export const TERRAIN_MAX_ZOOM = 16
export const HILLSHADE_SOURCE_ID = 'terrain3d-hillshade'
export const TERRAIN_SOURCE_ID = 'terrain3d-elevation'
export const HILLSHADE_LAYER_ID = 'terrain3d-hillshade-layer'

import type { MapStyleDto } from '@/api/dto'

/**
 * A basemap the switcher may offer.
 *
 * The list is **served** by `GET /api/config` (`MapStyleDto[]`); it is no longer compiled in. This
 * file used to hold a nine-entry table plus a `rasterStyle()` helper hand-building a MapLibre
 * wrapper around XYZ tile providers, and the mobile app held its own, different, copy. The server
 * now owns both — including the generated wrapper, which it serves on `/api/map/styles/{id}.json` —
 * so changing a tile provider is a restart rather than two releases.
 *
 * The id stays a plain string for the same reason: a union type would mean the server cannot add a
 * basemap without the web being rebuilt, which is exactly what this replaces.
 */
export type MapStyle = MapStyleDto
export type MapStyleId = string

/** Buckets the switcher renders as sections; label keys are `map.styles.group.<group>`. */
export type MapStyleGroup = 'vector' | 'satellite' | 'raster'

/** The groups this build knows how to name. Anything else is rendered under its raw value. */
const KNOWN_GROUPS: readonly string[] = ['vector', 'satellite', 'raster'] satisfies MapStyleGroup[]

export function isKnownGroup(group: string): group is MapStyleGroup {
  return KNOWN_GROUPS.includes(group)
}

/**
 * The style to render, given what the server serves and what the user picked.
 *
 * A remembered id that has since left the contract falls back to the first served basemap: better a
 * map than none.
 */
export function resolveMapStyle(
  styles: readonly MapStyle[],
  selectedId: MapStyleId | null
): MapStyle | undefined {
  return styles.find((style) => style.id === selectedId) ?? styles[0]
}

/**
 * The style document URL to load for a given colour scheme.
 *
 * A basemap with no dark counterpart is rendered as it is rather than hidden — a satellite view or a
 * scanned map has no night version, and dropping it from the map at dusk would be worse than
 * showing it.
 */
export function styleUrlFor(style: MapStyle, dark: boolean): string {
  return dark && style.darkVariant ? style.darkVariant : style.url
}

/** The served basemaps grouped for the switcher, in served order — the server does the sorting. */
export function groupStyles(styles: readonly MapStyle[]): { group: string; styles: MapStyle[] }[] {
  const sections: { group: string; styles: MapStyle[] }[] = []
  for (const style of styles) {
    const last = sections.at(-1)
    if (last && last.group === style.group) last.styles.push(style)
    else sections.push({ group: style.group, styles: [style] })
  }
  return sections
}
