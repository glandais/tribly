import { useState, useCallback, useMemo, useEffect } from 'react'
import { useComputedColorScheme } from '@mantine/core'
import type { StyleSpecification } from 'react-map-gl/maplibre'
import { MAP_STYLES, type MapStyleId, type MapStyle } from '../components/map/mapStyles'

const STORAGE_KEY = 'tribly-map-style'
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
  const [resolvedStyle, setResolvedStyle] = useState<string | StyleSpecification | undefined>(() => {
    const s = currentStyle.style
    return s instanceof Promise ? undefined : s
  })

  useEffect(() => {
    const s = currentStyle.style
    if (s instanceof Promise) {
      let cancelled = false
      s.then((resolved) => {
        if (!cancelled) setResolvedStyle(resolved)
      })
      return () => {
        cancelled = true
      }
    } else {
      setResolvedStyle(s)
    }
  }, [currentStyle.style])

  return {
    styleId,
    setStyleId,
    clearPreference,
    currentStyle,
    style: resolvedStyle,
  }
}
