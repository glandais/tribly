import { useCallback, useRef, useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import Map, {
  Source,
  Layer,
  MapRef,
  MapMouseEvent,
  NavigationControl,
  Marker,
  MarkerDragEvent,
} from 'react-map-gl/maplibre'
import maplibregl from 'maplibre-gl'
import { ArrowPathIcon, TrashIcon } from '@heroicons/react/24/outline'
import { MapStyleSwitcher } from '../map/MapStyleSwitcher'
import { useMapStyle } from '../../hooks/useMapStyle'
import { useRoutePlanner } from '../../hooks/useRoutePlanner'
import 'maplibre-gl/dist/maplibre-gl.css'

// Default center (France)
const DEFAULT_CENTER = { lng: -1.55, lat: 47.22 }
const DEFAULT_ZOOM = 12

export function RoutePlanner() {
  const { t } = useTranslation('planner')
  const { styleId, setStyleId, style } = useMapStyle()
  const mapRef = useRef<MapRef>(null)

  const {
    controlPoints,
    routeGeoJson,
    isLoading,
    error,
    addControlPoint,
    updateControlPoint,
    removeControlPoint,
    clearRoute,
  } = useRoutePlanner()

  const handleMarkerDragEnd = useCallback(
    (index: number) => (event: MarkerDragEvent) => {
      updateControlPoint(index, event.lngLat.lng, event.lngLat.lat)
    },
    [updateControlPoint]
  )

  const handleMarkerRightClick = useCallback(
    (index: number) => (event: React.MouseEvent) => {
      event.preventDefault()
      removeControlPoint(index)
    },
    [removeControlPoint]
  )

  const handleMapClick = useCallback(
    (event: MapMouseEvent) => {
      addControlPoint(event.lngLat.lng, event.lngLat.lat)
    },
    [addControlPoint]
  )

  // Extract route stats from BRouter GeoJSON
  const routeStats = useMemo(() => {
    if (!routeGeoJson?.features?.[0]?.properties) return null

    const props = routeGeoJson.features[0].properties
    const distance = props['track-length'] as number | undefined
    const ascend = props['plain-ascend'] as number | undefined

    if (distance === undefined) return null

    return {
      distance: distance / 1000, // Convert to km
      ascend: ascend ?? 0,
    }
  }, [routeGeoJson])

  return (
    <div className="flex flex-col h-full">
      {/* Toolbar */}
      <div className="flex items-center justify-between p-3 bg-white border-b">
        <div className="flex items-center gap-4">
          <h2 className="text-lg font-semibold text-gray-900">{t('title')}</h2>
          {controlPoints.length > 0 && (
            <span className="text-sm text-gray-500">
              {t('pointCount', { count: controlPoints.length })}
            </span>
          )}
          {isLoading && (
            <span className="flex items-center text-sm text-indigo-600">
              <ArrowPathIcon className="w-4 h-4 mr-1 animate-spin" />
              {t('calculating')}
            </span>
          )}
          {error && <span className="text-sm text-red-600">{error}</span>}
          {routeStats && (
            <div className="flex items-center gap-3 text-sm">
              <span className="font-medium text-gray-900">
                {routeStats.distance.toFixed(1)} {t('units.km')}
              </span>
              <span className="text-green-600">+{Math.round(routeStats.ascend)} {t('units.m')}</span>
            </div>
          )}
        </div>
        <div className="flex items-center gap-2">
          {controlPoints.length > 0 && (
            <button
              onClick={clearRoute}
              className="inline-flex items-center px-3 py-1.5 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
            >
              <TrashIcon className="w-4 h-4 mr-1" />
              {t('clear')}
            </button>
          )}
        </div>
      </div>

      {/* Map */}
      <div className="relative flex-1">
        <Map
          ref={mapRef}
          mapLib={maplibregl}
          mapStyle={style}
          initialViewState={{
            longitude: DEFAULT_CENTER.lng,
            latitude: DEFAULT_CENTER.lat,
            zoom: DEFAULT_ZOOM,
          }}
          style={{ width: '100%', height: '100%' }}
          onClick={handleMapClick}
          cursor="crosshair"
        >
          <NavigationControl position="top-left" />
          <MapStyleSwitcher
            position="top-right"
            currentStyleId={styleId}
            onStyleChange={setStyleId}
          />

          {/* Route line */}
          {routeGeoJson && (
            <Source id="route" type="geojson" data={routeGeoJson}>
              <Layer
                id="route-line"
                type="line"
                paint={{
                  'line-color': '#4F46E5',
                  'line-width': 5,
                  'line-opacity': 0.8,
                }}
              />
            </Source>
          )}

          {/* Control point markers */}
          {controlPoints.map((point, index) => {
            const isFirst = index === 0
            const isLast = index === controlPoints.length - 1 && controlPoints.length > 1

            let markerColor = 'bg-amber-500' // intermediate waypoints
            if (isFirst) markerColor = 'bg-green-500'
            else if (isLast) markerColor = 'bg-red-500'

            return (
              <Marker
                key={`point-${index}`}
                longitude={point.lng}
                latitude={point.lat}
                anchor="center"
                draggable
                onDragEnd={handleMarkerDragEnd(index)}
              >
                <div
                  className="flex items-center cursor-grab active:cursor-grabbing"
                  onContextMenu={handleMarkerRightClick(index)}
                >
                  <div
                    className={`w-6 h-6 ${markerColor} border-2 border-white rounded-full shadow-lg`}
                  />
                  {!isFirst && !isLast && (
                    <span className="ml-1 px-1 bg-white/90 rounded text-xs font-medium shadow-sm">
                      {index}
                    </span>
                  )}
                </div>
              </Marker>
            )
          })}
        </Map>

        {/* Instructions overlay */}
        {controlPoints.length === 0 && (
          <div className="absolute bottom-4 left-1/2 -translate-x-1/2 px-4 py-2 bg-white/90 rounded-lg shadow-lg">
            <p className="text-sm text-gray-600">{t('instructions')}</p>
          </div>
        )}
      </div>
    </div>
  )
}
