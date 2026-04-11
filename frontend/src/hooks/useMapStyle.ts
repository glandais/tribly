import { useState, useEffect } from 'react'
import { useComputedColorScheme } from '@mantine/core'
import type { StyleSpecification } from 'react-map-gl/maplibre'
import { MAP_STYLES, type MapStyleId, type MapStyle } from '../components/map/mapStyles'
import { useMapStyleStore } from '@/store/mapStyleStore'

const DEFAULT_LIGHT_STYLE: MapStyleId = 'graybeard'
const DEFAULT_DARK_STYLE: MapStyleId = 'eclipse'

export function useMapStyle() {
  const colorScheme = useComputedColorScheme('light')
  const defaultStyle = colorScheme === 'dark' ? DEFAULT_DARK_STYLE : DEFAULT_LIGHT_STYLE

  const {
    savedStyleId,
    terrain3d,
    hillshade,
    setStyleId,
    clearStylePreference,
    setTerrain3d,
    setHillshade,
  } = useMapStyleStore()

  const styleId = savedStyleId ?? defaultStyle

  const currentStyle: MapStyle = MAP_STYLES[styleId]

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
    clearPreference: clearStylePreference,
    currentStyle,
    style,
    terrain3d,
    setTerrain3d,
    hillshade,
    setHillshade,
  }
}
