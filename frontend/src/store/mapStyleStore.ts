import { create } from 'zustand'
import { MAP_STYLES, type MapStyleId } from '@/components/map/mapStyles'

const STYLE_KEY = 'pedalons-map-style'
const TERRAIN_KEY = 'pedalons-map-terrain3d'
const HILLSHADE_KEY = 'pedalons-map-hillshade'

function getSavedStyleId(): MapStyleId | null {
  if (typeof window === 'undefined') return null
  const saved = localStorage.getItem(STYLE_KEY) as MapStyleId | null
  return saved && MAP_STYLES[saved] ? saved : null
}

interface MapStyleState {
  savedStyleId: MapStyleId | null
  terrain3d: boolean
  hillshade: boolean
  setStyleId: (id: MapStyleId) => void
  clearStylePreference: () => void
  setTerrain3d: (enabled: boolean) => void
  setHillshade: (enabled: boolean) => void
}

export const useMapStyleStore = create<MapStyleState>()((set) => ({
  savedStyleId: getSavedStyleId(),
  terrain3d: typeof window !== 'undefined' && localStorage.getItem(TERRAIN_KEY) === 'true',
  hillshade: typeof window !== 'undefined' && localStorage.getItem(HILLSHADE_KEY) === 'true',

  setStyleId: (id) => {
    localStorage.setItem(STYLE_KEY, id)
    set({ savedStyleId: id })
  },

  clearStylePreference: () => {
    localStorage.removeItem(STYLE_KEY)
    set({ savedStyleId: null })
  },

  setTerrain3d: (enabled) => {
    localStorage.setItem(TERRAIN_KEY, String(enabled))
    set({ terrain3d: enabled })
  },

  setHillshade: (enabled) => {
    localStorage.setItem(HILLSHADE_KEY, String(enabled))
    set({ hillshade: enabled })
  },
}))
