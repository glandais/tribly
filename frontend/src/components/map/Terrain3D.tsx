import { useEffect } from 'react'
import { useMap } from 'react-map-gl/maplibre'
import type { MapTerrainDto } from '@/api/dto'
import { HILLSHADE_SOURCE_ID, HILLSHADE_LAYER_ID, TERRAIN_SOURCE_ID } from './mapStyles'

interface Terrain3DProps {
  /** The served elevation source. Undefined when the deployment configures none. */
  source: MapTerrainDto | undefined
  terrain: boolean
  hillshade: boolean
}

export function Terrain3D({ source, terrain, hillshade }: Terrain3DProps) {
  const { current: mapRef } = useMap()
  const url = source?.url
  const maxzoom = source?.maxZoom

  useEffect(() => {
    if (!mapRef) return
    const map = mapRef.getMap()

    const apply = () => {
      // Hillshade source — needed by hillshade layer
      if (hillshade && url) {
        if (!map.getSource(HILLSHADE_SOURCE_ID)) {
          map.addSource(HILLSHADE_SOURCE_ID, { type: 'raster-dem', url, maxzoom })
        }
        if (!map.getLayer(HILLSHADE_LAYER_ID)) {
          map.addLayer({
            id: HILLSHADE_LAYER_ID,
            type: 'hillshade',
            source: HILLSHADE_SOURCE_ID,
            paint: {
              'hillshade-method': 'igor',
              'hillshade-exaggeration': 0.4,
              'hillshade-highlight-color': 'rgb(255, 255, 228)',
              'hillshade-shadow-color': 'rgb(114, 124, 131)',
            },
          })
        }
      } else {
        if (map.getLayer(HILLSHADE_LAYER_ID)) map.removeLayer(HILLSHADE_LAYER_ID)
        if (map.getSource(HILLSHADE_SOURCE_ID)) map.removeSource(HILLSHADE_SOURCE_ID)
      }

      // Terrain source — needed by 3D terrain
      if (terrain && url) {
        if (!map.getSource(TERRAIN_SOURCE_ID)) {
          map.addSource(TERRAIN_SOURCE_ID, { type: 'raster-dem', url, maxzoom })
        }
        map.setTerrain({ source: TERRAIN_SOURCE_ID, exaggeration: 1 })
      } else {
        map.setTerrain(null)
        if (map.getSource(TERRAIN_SOURCE_ID)) map.removeSource(TERRAIN_SOURCE_ID)
      }
    }

    if (map.isStyleLoaded()) {
      apply()
    }

    map.on('style.load', apply)
    return () => {
      map.off('style.load', apply)
    }
  }, [terrain, hillshade, url, maxzoom, mapRef])

  return null
}
