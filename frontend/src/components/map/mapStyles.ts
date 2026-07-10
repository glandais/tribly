export const TERRAIN_URL = 'https://tiles.mapterhorn.com/tilejson.json'
export const TERRAIN_MAX_ZOOM = 16
export const HILLSHADE_SOURCE_ID = 'terrain3d-hillshade'
export const TERRAIN_SOURCE_ID = 'terrain3d-elevation'
export const HILLSHADE_LAYER_ID = 'terrain3d-hillshade-layer'

import { colorful, graybeard, eclipse, neutrino, shadow, satellite } from '@versatiles/style'
import { StyleSpecification } from 'react-map-gl/maplibre'

export type MapStyleId =
  'colorful' | 'graybeard' | 'eclipse' | 'neutrino' | 'shadow' | 'satellite' | 'ign-vector'

export interface MapStyle {
  id: MapStyleId
  name: string
  style: string | StyleSpecification | Promise<StyleSpecification>
  isDark: boolean
}

const BASE_URL = 'https://tiles.versatiles.org'
const layerOptions = {
  baseUrl: BASE_URL,
}

const IGN_VECTOR_URL =
  'https://data.geopf.fr/annexes/ressources/vectorTiles/styles/PLAN.IGN/standard.json'

export const MAP_STYLES: Record<MapStyleId, MapStyle> = {
  colorful: {
    id: 'colorful',
    name: 'Colorful',
    style: colorful(layerOptions),
    isDark: false,
  },
  graybeard: {
    id: 'graybeard',
    name: 'Graybeard',
    style: graybeard(layerOptions),
    isDark: false,
  },
  eclipse: {
    id: 'eclipse',
    name: 'Eclipse',
    style: eclipse(layerOptions),
    isDark: true,
  },
  neutrino: {
    id: 'neutrino',
    name: 'Neutrino',
    style: neutrino(layerOptions),
    isDark: false,
  },
  shadow: {
    id: 'shadow',
    name: 'Shadow',
    style: shadow(layerOptions),
    isDark: true,
  },
  'ign-vector': {
    id: 'ign-vector',
    name: 'IGN (France)',
    style: fetch(IGN_VECTOR_URL).then((r) => r.json() as Promise<StyleSpecification>),
    isDark: false,
  },
  satellite: {
    id: 'satellite',
    name: 'Satellite',
    style: graybeard(layerOptions), // placeholder until async style loads
    isDark: false,
  },
}

// satellite() returns a Promise in @versatiles/style v5 — resolve it eagerly
satellite(layerOptions).then((style: StyleSpecification) => {
  MAP_STYLES.satellite.style = style
})

export const DEFAULT_STYLE_ID: MapStyleId = 'graybeard'
export const STYLE_IDS = Object.keys(MAP_STYLES) as MapStyleId[]
