import { useCallback, useRef, useMemo, useState } from 'react'
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

// Find the closest point on a line segment to a given point
function closestPointOnSegment(
  px: number,
  py: number,
  x1: number,
  y1: number,
  x2: number,
  y2: number
): { x: number; y: number; dist: number } {
  const dx = x2 - x1
  const dy = y2 - y1
  const lengthSq = dx * dx + dy * dy

  if (lengthSq === 0) {
    // Segment is a point
    const dist = Math.sqrt((px - x1) ** 2 + (py - y1) ** 2)
    return { x: x1, y: y1, dist }
  }

  // Project point onto line, clamping to segment
  const t = Math.max(0, Math.min(1, ((px - x1) * dx + (py - y1) * dy) / lengthSq))
  const closestX = x1 + t * dx
  const closestY = y1 + t * dy
  const dist = Math.sqrt((px - closestX) ** 2 + (py - closestY) ** 2)

  return { x: closestX, y: closestY, dist }
}

// Find closest point on a LineString to a given point
function closestPointOnLine(
  point: { lng: number; lat: number },
  coordinates: number[][]
): { lng: number; lat: number } {
  let closest = { lng: coordinates[0][0], lat: coordinates[0][1] }
  let minDist = Infinity

  for (let i = 0; i < coordinates.length - 1; i++) {
    const result = closestPointOnSegment(
      point.lng,
      point.lat,
      coordinates[i][0],
      coordinates[i][1],
      coordinates[i + 1][0],
      coordinates[i + 1][1]
    )
    if (result.dist < minDist) {
      minDist = result.dist
      closest = { lng: result.x, lat: result.y }
    }
  }

  return closest
}

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
    insertControlPoint,
    updateControlPoint,
    removeControlPoint,
    clearRoute,
  } = useRoutePlanner()

  // State for hover marker on route segments
  const [hoverPoint, setHoverPoint] = useState<{
    lng: number
    lat: number
    segmentIndex: number
  } | null>(null)

  // State for dragging a ghost point (before insertion)
  const [draggingGhost, setDraggingGhost] = useState<{
    lng: number
    lat: number
    segmentIndex: number
  } | null>(null)

  // Track if we're dragging an existing marker
  const [isDraggingMarker, setIsDraggingMarker] = useState(false)

  const handleMarkerDragStart = useCallback(() => {
    setIsDraggingMarker(true)
    setHoverPoint(null)
  }, [])

  const handleMarkerDragEnd = useCallback(
    (index: number) => (event: MarkerDragEvent) => {
      updateControlPoint(index, event.lngLat.lng, event.lngLat.lat)
      setIsDraggingMarker(false)
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
      // Don't add point if clicking on route (handled by ghost marker)
      if (hoverPoint) return
      addControlPoint(event.lngLat.lng, event.lngLat.lat)
    },
    [addControlPoint, hoverPoint]
  )

  // Handle mouse move to detect hover on route segments
  const handleMouseMove = useCallback(
    (event: MapMouseEvent) => {
      // If dragging a ghost point, update its position
      if (draggingGhost) {
        setDraggingGhost((prev) =>
          prev ? { ...prev, lng: event.lngLat.lng, lat: event.lngLat.lat } : null
        )
        return
      }

      // Don't show hover while dragging an existing marker
      if (isDraggingMarker) return

      if (!mapRef.current || !routeGeoJson) return

      // Check if cursor is near an existing control point (within 20px)
      const MARKER_PROXIMITY_PX = 20
      for (const point of controlPoints) {
        const projected = mapRef.current.project([point.lng, point.lat])
        const dx = projected.x - event.point.x
        const dy = projected.y - event.point.y
        const distance = Math.sqrt(dx * dx + dy * dy)
        if (distance < MARKER_PROXIMITY_PX) {
          // Too close to existing marker, don't show ghost
          setHoverPoint(null)
          return
        }
      }

      // Query features near the cursor (with buffer for easier selection)
      const ROUTE_BUFFER_PX = 15
      const bbox: [[number, number], [number, number]] = [
        [event.point.x - ROUTE_BUFFER_PX, event.point.y - ROUTE_BUFFER_PX],
        [event.point.x + ROUTE_BUFFER_PX, event.point.y + ROUTE_BUFFER_PX],
      ]
      const features = mapRef.current.queryRenderedFeatures(bbox, {
        layers: ['route-line'],
      })

      if (features.length > 0) {
        const feature = features[0]
        const segmentIndex = feature.properties?.segmentIndex as number | undefined
        if (segmentIndex !== undefined && feature.geometry.type === 'LineString') {
          // Snap to the closest point on the route segment
          const coordinates = (feature.geometry as GeoJSON.LineString).coordinates
          const snapped = closestPointOnLine(
            { lng: event.lngLat.lng, lat: event.lngLat.lat },
            coordinates
          )
          setHoverPoint({
            lng: snapped.lng,
            lat: snapped.lat,
            segmentIndex,
          })
          return
        }
      }

      setHoverPoint(null)
    },
    [routeGeoJson, draggingGhost, isDraggingMarker, controlPoints]
  )

  const handleMouseLeave = useCallback(() => {
    setHoverPoint(null)
    // Don't clear draggingGhost - user might drag outside momentarily
  }, [])

  // Handle map mousedown - start dragging ghost if hovering over route
  const handleMapMouseDown = useCallback(
    (event: MapMouseEvent) => {
      // Only respond to left click (button 0)
      if (event.originalEvent.button !== 0) return
      if (!hoverPoint) return

      // Prevent default map drag behavior
      event.preventDefault()

      // Start dragging the ghost
      setDraggingGhost({
        lng: hoverPoint.lng,
        lat: hoverPoint.lat,
        segmentIndex: hoverPoint.segmentIndex,
      })
      setHoverPoint(null)
    },
    [hoverPoint]
  )

  // Handle mouseup - insert the point if we were dragging
  const handleMouseUp = useCallback(() => {
    if (draggingGhost) {
      insertControlPoint(draggingGhost.segmentIndex, draggingGhost.lng, draggingGhost.lat)
      setDraggingGhost(null)
    }
  }, [draggingGhost, insertControlPoint])

  // Extract route stats from FeatureCollection properties
  const routeStats = useMemo(() => {
    const props = routeGeoJson?.properties
    if (!props) return null

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
              <span className="text-green-600">
                +{Math.round(routeStats.ascend)} {t('units.m')}
              </span>
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
          onMouseDown={handleMapMouseDown}
          onMouseMove={handleMouseMove}
          onMouseLeave={handleMouseLeave}
          onMouseUp={handleMouseUp}
          interactiveLayerIds={routeGeoJson ? ['route-line'] : []}
          cursor={draggingGhost ? 'grabbing' : hoverPoint ? 'pointer' : 'crosshair'}
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
                key={point.id}
                longitude={point.lng}
                latitude={point.lat}
                anchor="center"
                draggable
                onDragStart={handleMarkerDragStart}
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

          {/* Ghost marker for inserting new points on route */}
          {(hoverPoint || draggingGhost) && (
            <Marker
              longitude={(draggingGhost || hoverPoint)!.lng}
              latitude={(draggingGhost || hoverPoint)!.lat}
              anchor="center"
            >
              <div
                className={`w-5 h-5 border-2 border-white rounded-full shadow-lg transition-opacity pointer-events-none ${
                  draggingGhost ? 'bg-indigo-600 opacity-100' : 'bg-indigo-400 opacity-70'
                }`}
              />
            </Marker>
          )}
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
