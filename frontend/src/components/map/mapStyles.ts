import { colorful, graybeard, eclipse, neutrino, shadow } from '@versatiles/style'
import { StyleSpecification } from 'react-map-gl/maplibre'

export type MapStyleId = 'colorful' | 'graybeard' | 'eclipse' | 'neutrino' | 'shadow'

export interface MapStyle {
  id: MapStyleId
  name: string
  style: string | StyleSpecification
  isDark: boolean
}

const BASE_URL = 'https://tiles.versatiles.org'
const layerOptions = {
  baseUrl: BASE_URL,
}

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
}

export const DEFAULT_STYLE_ID: MapStyleId = 'graybeard'
export const STYLE_IDS = Object.keys(MAP_STYLES) as MapStyleId[]
