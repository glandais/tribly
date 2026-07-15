export const TERRAIN_URL = 'https://tiles.mapterhorn.com/tilejson.json'
export const TERRAIN_MAX_ZOOM = 16
export const HILLSHADE_SOURCE_ID = 'terrain3d-hillshade'
export const TERRAIN_SOURCE_ID = 'terrain3d-elevation'
export const HILLSHADE_LAYER_ID = 'terrain3d-hillshade-layer'

import { colorful, graybeard, eclipse, satellite } from '@versatiles/style'
import { StyleSpecification } from 'react-map-gl/maplibre'

export type MapStyleId =
  | 'colorful'
  | 'eclipse'
  | 'satellite'
  | 'ign-vector'
  | 'osm'
  | 'cyclosm'
  | 'esri-satellite'
  | 'ign-satellite'
  | 'ign-scan25'

/** Buckets the switcher renders as sections; label keys are `map.styles.group.<group>`. */
export type MapStyleGroup = 'vector' | 'satellite' | 'raster'

/** Display order of the groups in the switcher. */
export const STYLE_GROUPS: MapStyleGroup[] = ['vector', 'satellite', 'raster']

export interface MapStyle {
  id: MapStyleId
  name: string
  style: string | StyleSpecification | Promise<StyleSpecification>
  isDark: boolean
  group: MapStyleGroup
}

const BASE_URL = 'https://tiles.versatiles.org'
const layerOptions = {
  baseUrl: BASE_URL,
}

const IGN_VECTOR_URL =
  'https://data.geopf.fr/annexes/ressources/vectorTiles/styles/PLAN.IGN/standard.json'

/**
 * Wraps a raster XYZ tile source in a minimal MapLibre style, so classic Leaflet-style basemaps
 * (imported from biketeam) sit alongside the vector styles. MapLibre has no `{s}` subdomain token,
 * so pass each subdomain as its own URL in the array. `maxzoom` is the deepest native zoom; MapLibre
 * overzooms past it, keeping the route lines and terrain overlay usable at higher zooms. `minzoom`
 * suppresses tile requests below the source's shallowest matrix (e.g. IGN SCAN 25 starts at z6).
 */
const rasterStyle = (
  tiles: string | string[],
  attribution: string,
  maxzoom = 19,
  minzoom?: number
): StyleSpecification => ({
  version: 8,
  sources: {
    raster: {
      type: 'raster',
      tiles: Array.isArray(tiles) ? tiles : [tiles],
      tileSize: 256,
      ...(minzoom !== undefined && { minzoom }),
      maxzoom,
      attribution,
    },
  },
  layers: [{ id: 'raster', type: 'raster', source: 'raster' }],
})

const OSM_ATTRIBUTION =
  '&copy; <a href="https://www.openstreetmap.org/copyright" target="_blank">OpenStreetMap</a>'
const IGN_ATTRIBUTION =
  '<a target="_blank" href="https://www.geoportail.gouv.fr/">Geoportail France</a>'

export const MAP_STYLES: Record<MapStyleId, MapStyle> = {
  colorful: {
    id: 'colorful',
    name: 'Light',
    style: colorful(layerOptions),
    isDark: false,
    group: 'vector',
  },
  eclipse: {
    id: 'eclipse',
    name: 'Dark',
    style: eclipse(layerOptions),
    isDark: true,
    group: 'vector',
  },
  'ign-vector': {
    id: 'ign-vector',
    name: 'IGN (France)',
    style: fetch(IGN_VECTOR_URL).then((r) => r.json() as Promise<StyleSpecification>),
    isDark: false,
    group: 'vector',
  },
  satellite: {
    id: 'satellite',
    name: 'Satellite (VersaTiles)',
    style: graybeard(layerOptions), // placeholder until async style loads
    isDark: false,
    group: 'satellite',
  },
  osm: {
    id: 'osm',
    name: 'OpenStreetMap',
    style: rasterStyle('https://tile.openstreetmap.org/{z}/{x}/{y}.png', OSM_ATTRIBUTION, 19),
    isDark: false,
    group: 'raster',
  },
  cyclosm: {
    id: 'cyclosm',
    name: 'CyclOSM',
    style: rasterStyle(
      [
        'https://a.tile-cyclosm.openstreetmap.fr/cyclosm/{z}/{x}/{y}.png',
        'https://b.tile-cyclosm.openstreetmap.fr/cyclosm/{z}/{x}/{y}.png',
        'https://c.tile-cyclosm.openstreetmap.fr/cyclosm/{z}/{x}/{y}.png',
      ],
      '<a href="https://github.com/cyclosm/cyclosm-cartocss-style/releases" title="CyclOSM - Open Bicycle render">CyclOSM</a> | ' +
        OSM_ATTRIBUTION,
      17
    ),
    isDark: false,
    group: 'raster',
  },
  'esri-satellite': {
    id: 'esri-satellite',
    name: 'Satellite (ESRI)',
    style: rasterStyle(
      'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}',
      'Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community',
      19
    ),
    isDark: false,
    group: 'satellite',
  },
  'ign-satellite': {
    id: 'ign-satellite',
    name: 'Satellite (IGN)',
    style: rasterStyle(
      'https://data.geopf.fr/wmts?SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetTile&STYLE=normal&FORMAT=image/jpeg&LAYER=ORTHOIMAGERY.ORTHOPHOTOS&TILEMATRIXSET=PM&TILEMATRIX={z}&TILECOL={x}&TILEROW={y}',
      IGN_ATTRIBUTION,
      19
    ),
    isDark: false,
    group: 'satellite',
  },
  // SCAN 25 is IGN's topographic map. It sits behind the /private endpoint and needs the shared
  // transitional key `ign_scan_ws` (IGN's public stopgap for non-open SCAN data — still live as of
  // 2026; the long-term path is a per-account HASH key from cartes.gouv.fr). France-only, native
  // zooms ~6–16, so `minzoom` avoids 404s at world zoom.
  'ign-scan25': {
    id: 'ign-scan25',
    name: 'IGN SCAN 25 (France)',
    style: rasterStyle(
      'https://data.geopf.fr/private/wmts?apikey=ign_scan_ws&SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetTile&STYLE=normal&FORMAT=image/jpeg&LAYER=GEOGRAPHICALGRIDSYSTEMS.MAPS.SCAN25TOUR&TILEMATRIXSET=PM&TILEMATRIX={z}&TILECOL={x}&TILEROW={y}',
      IGN_ATTRIBUTION,
      16,
      6
    ),
    isDark: false,
    group: 'raster',
  },
}

// satellite() returns a Promise in @versatiles/style v5 — resolve it eagerly
satellite(layerOptions).then((style: StyleSpecification) => {
  MAP_STYLES.satellite.style = style
})

export const DEFAULT_STYLE_ID: MapStyleId = 'colorful'
export const STYLE_IDS = Object.keys(MAP_STYLES) as MapStyleId[]
