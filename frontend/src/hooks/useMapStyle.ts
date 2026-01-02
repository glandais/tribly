import { useState, useCallback, useMemo } from 'react'
import {
  MAP_STYLES,
  DEFAULT_STYLE_ID,
  type MapStyleId,
  type MapStyle,
} from '../components/map/mapStyles'

const STORAGE_KEY = 'tribly-map-style'

function getInitialStyleId(): MapStyleId {
  if (typeof window === 'undefined') return DEFAULT_STYLE_ID

  const saved = localStorage.getItem(STORAGE_KEY) as MapStyleId | null
  if (saved && MAP_STYLES[saved]) {
    return saved
  }
  return DEFAULT_STYLE_ID
}

export function useMapStyle() {
  const [styleId, setStyleIdState] = useState<MapStyleId>(getInitialStyleId)

  const setStyleId = useCallback((id: MapStyleId) => {
    setStyleIdState(id)
    localStorage.setItem(STORAGE_KEY, id)
  }, [])

  const currentStyle: MapStyle = useMemo(() => MAP_STYLES[styleId], [styleId])

  return {
    styleId,
    setStyleId,
    currentStyle,
    style: currentStyle.style,
  }
}
