import { useEffect } from 'react'
import { useMap } from 'react-map-gl/maplibre'
import {
  HILLSHADE_SOURCE_ID,
  HILLSHADE_LAYER_ID,
  TERRAIN_SOURCE_ID,
  TERRAIN_URL,
  TERRAIN_MAX_ZOOM,
} from './mapStyles'

interface Terrain3DProps {
  terrain: boolean
  hillshade: boolean
}

export function Terrain3D({ terrain, hillshade }: Terrain3DProps) {
  const { current: mapRef } = useMap()

  useEffect(() => {
    if (!mapRef) return
    const map = mapRef.getMap()

    const apply = () => {
      // Hillshade source — needed by hillshade layer
      if (hillshade) {
        if (!map.getSource(HILLSHADE_SOURCE_ID)) {
          map.addSource(HILLSHADE_SOURCE_ID, {
            type: 'raster-dem',
            url: TERRAIN_URL,
            maxzoom: TERRAIN_MAX_ZOOM,
          })
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
      if (terrain) {
        if (!map.getSource(TERRAIN_SOURCE_ID)) {
          map.addSource(TERRAIN_SOURCE_ID, {
            type: 'raster-dem',
            url: TERRAIN_URL,
            maxzoom: TERRAIN_MAX_ZOOM,
          })
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
  }, [terrain, hillshade, mapRef])

  return null
}
