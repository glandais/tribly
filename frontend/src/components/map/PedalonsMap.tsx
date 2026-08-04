import { forwardRef, useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import Map, { NavigationControl } from 'react-map-gl/maplibre'
import type { MapProps, MapRef } from 'react-map-gl/maplibre'
import * as maplibregl from 'maplibre-gl'
import './setupMaplibreWorker'
import { Terrain3D } from './Terrain3D'
import { MapStyleSwitcher } from './MapStyleSwitcher'
import { useMapStyle } from '@/hooks/useMapStyle'

/**
 * Every map control in this app lives in ONE column, top-left, in one visual language: the zoom
 * buttons, then the basemap switcher, then whatever the page adds through `MapControlGroup`.
 * There is deliberately no per-map position prop — a control that moves between pages is a control
 * the visitor has to look for.
 */
type PedalonsMapProps = Omit<MapProps, 'mapLib' | 'mapStyle' | 'style'>

export const PedalonsMap = forwardRef<MapRef, PedalonsMapProps>(({ children, ...props }, ref) => {
  const { t } = useTranslation()
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

  // MapLibre's own controls (zoom, compass, fullscreen) label themselves in English otherwise.
  // `locale` is a map *construction* option: switching language re-labels the next map to mount,
  // not one already on screen. Fine for tooltips, but don't read this as reactive.
  const locale = useMemo(
    () => ({
      'FullscreenControl.Enter': t('map.fullscreen.enter'),
      'FullscreenControl.Exit': t('map.fullscreen.exit'),
      'NavigationControl.ZoomIn': t('map.zoomIn'),
      'NavigationControl.ZoomOut': t('map.zoomOut'),
      'NavigationControl.ResetBearing': t('map.resetBearing'),
    }),
    [t]
  )

  return (
    <Map
      ref={ref}
      mapLib={maplibregl}
      mapStyle={style}
      locale={locale}
      style={{ width: '100%', height: '100%' }}
      {...props}
    >
      <NavigationControl position="top-left" />
      <Terrain3D source={terrainSource} terrain={terrain3d} hillshade={hillshade} />
      <MapStyleSwitcher
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
})
PedalonsMap.displayName = 'PedalonsMap'
