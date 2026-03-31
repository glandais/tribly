import { forwardRef } from 'react'
import Map, { NavigationControl } from 'react-map-gl/maplibre'
import type { MapProps, MapRef } from 'react-map-gl/maplibre'
import maplibregl from 'maplibre-gl'
import { Terrain3D } from './Terrain3D'
import { MapStyleSwitcher } from './MapStyleSwitcher'
import { useMapStyle } from '@/hooks/useMapStyle'

type TriblyMapProps = Omit<MapProps, 'mapLib' | 'mapStyle' | 'style'> & {
  mapStyleSwitcherPosition?: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
  navigationControlPosition?: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
}

export const TriblyMap = forwardRef<MapRef, TriblyMapProps>(
  (
    {
      mapStyleSwitcherPosition = 'bottom-left',
      navigationControlPosition = 'top-left',
      children,
      ...props
    },
    ref
  ) => {
    const { styleId, setStyleId, style, terrain3d, setTerrain3d, hillshade, setHillshade } =
      useMapStyle()

    return (
      <Map
        ref={ref}
        mapLib={maplibregl}
        mapStyle={style}
        style={{ width: '100%', height: '100%' }}
        {...props}
      >
        <NavigationControl position={navigationControlPosition} />
        <Terrain3D terrain={terrain3d} hillshade={hillshade} />
        <MapStyleSwitcher
          position={mapStyleSwitcherPosition}
          currentStyleId={styleId}
          onStyleChange={setStyleId}
          terrain3d={terrain3d}
          onTerrain3DChange={setTerrain3d}
          hillshade={hillshade}
          onHillshadeChange={setHillshade}
        />
        {children}
      </Map>
    )
  }
)
TriblyMap.displayName = 'TriblyMap'
