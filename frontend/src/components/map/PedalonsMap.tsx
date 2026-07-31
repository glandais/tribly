import { forwardRef } from 'react'
import Map, { NavigationControl } from 'react-map-gl/maplibre'
import type { MapProps, MapRef } from 'react-map-gl/maplibre'
import maplibregl from 'maplibre-gl'
import { Terrain3D } from './Terrain3D'
import { MapStyleSwitcher } from './MapStyleSwitcher'
import { useMapStyle } from '@/hooks/useMapStyle'

type PedalonsMapProps = Omit<MapProps, 'mapLib' | 'mapStyle' | 'style'> & {
  mapStyleSwitcherPosition?: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
  navigationControlPosition?: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
}

export const PedalonsMap = forwardRef<MapRef, PedalonsMapProps>(
  (
    {
      mapStyleSwitcherPosition = 'bottom-left',
      navigationControlPosition = 'top-left',
      children,
      ...props
    },
    ref
  ) => {
    const {
      styleId,
      styles,
      terrainSource,
      setStyleId,
      style,
      terrain3d,
      setTerrain3d,
      hillshade,
      setHillshade,
    } = useMapStyle()

    return (
      <Map
        ref={ref}
        mapLib={maplibregl}
        mapStyle={style}
        style={{ width: '100%', height: '100%' }}
        {...props}
      >
        <NavigationControl position={navigationControlPosition} />
        <Terrain3D source={terrainSource} terrain={terrain3d} hillshade={hillshade} />
        <MapStyleSwitcher
          position={mapStyleSwitcherPosition}
          styles={styles}
          currentStyleId={styleId}
          onStyleChange={setStyleId}
          terrain3d={terrain3d}
          hillshade={hillshade}
          // No served elevation source, no relief controls: the switches would toggle nothing.
          onTerrain3DChange={terrainSource ? setTerrain3d : undefined}
          onHillshadeChange={terrainSource ? setHillshade : undefined}
        />
        {children}
      </Map>
    )
  }
)
PedalonsMap.displayName = 'PedalonsMap'
