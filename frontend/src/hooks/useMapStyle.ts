import { useState, useCallback, useMemo, useEffect } from 'react'
import { useComputedColorScheme } from '@mantine/core'
import type { StyleSpecification } from 'react-map-gl/maplibre'
import { MAP_STYLES, type MapStyleId, type MapStyle } from '../components/map/mapStyles'

const STORAGE_KEY = 'pedalons-map-style'
const DEFAULT_LIGHT_STYLE: MapStyleId = 'graybeard'
const DEFAULT_DARK_STYLE: MapStyleId = 'eclipse'

function getSavedStyleId(): MapStyleId | null {
  if (typeof window === 'undefined') return null

  const saved = localStorage.getItem(STORAGE_KEY) as MapStyleId | null
  if (saved && MAP_STYLES[saved]) {
    return saved
  }
  return null
}

export function useMapStyle() {
  const colorScheme = useComputedColorScheme('light')
  const defaultStyle = colorScheme === 'dark' ? DEFAULT_DARK_STYLE : DEFAULT_LIGHT_STYLE

  // Track user's manual preference (null means follow color scheme)
  const [savedStyleId, setSavedStyleId] = useState<MapStyleId | null>(() => getSavedStyleId())

  // Effective styleId: use saved preference if exists, otherwise follow color scheme
  const styleId = savedStyleId ?? defaultStyle

  const setStyleId = useCallback((id: MapStyleId) => {
    setSavedStyleId(id)
    localStorage.setItem(STORAGE_KEY, id)
  }, [])

  const clearPreference = useCallback(() => {
    localStorage.removeItem(STORAGE_KEY)
    setSavedStyleId(null)
  }, [])

  const currentStyle: MapStyle = useMemo(() => MAP_STYLES[styleId], [styleId])

  // Resolve async styles (e.g. satellite) into a sync value
  const [asyncStyle, setAsyncStyle] = useState<StyleSpecification | undefined>(undefined)

  const rawStyle = currentStyle.style
  const isAsync = rawStyle instanceof Promise

  useEffect(() => {
    if (!isAsync) return
    let cancelled = false
    rawStyle.then((resolved) => {
      if (!cancelled) setAsyncStyle(resolved)
    })
    return () => {
      cancelled = true
    }
  }, [rawStyle, isAsync])

  const style = isAsync ? asyncStyle : rawStyle

  return {
    styleId,
    setStyleId,
    clearPreference,
    currentStyle,
    style,
  }
}
