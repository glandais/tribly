import { useCallback, useRef, useMemo, useState, useEffect } from 'react'
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
import type { GeoPoint } from '../../api/api'
import 'maplibre-gl/dist/maplibre-gl.css'
import { findPreviousControlPointIndex } from '@/lib/planner'
import { around } from 'geokdbush'

// Default center (France)
const DEFAULT_CENTER = { lng: -1.55, lat: 47.22 }
const DEFAULT_ZOOM = 12

interface EmbeddedRoutePlannerProps {
  onPointsChange: (points: GeoPoint[]) => void
  initialTrack?: number[][] // [lng, lat, ele, dist][] from existing route
}

export function EmbeddedRoutePlanner({ onPointsChange, initialTrack }: EmbeddedRoutePlannerProps) {
  const { t } = useTranslation('planner')
  const { styleId, setStyleId, style } = useMapStyle()
  const mapRef = useRef<MapRef>(null)

  const {
    route,
    controlPoints,
    routeGeoJson,
    isLoading,
    error,
    addControlPoint,
    insertControlPoint,
    updateControlPoint,
    removeControlPoint,
    updateZoom,
    clearRoute,
  } = useRoutePlanner({ initialTrack })

  // Calculate initial view state from track bounds
  const initialViewState = useMemo(() => {
    if (!initialTrack || initialTrack.length === 0) {
      return {
        longitude: DEFAULT_CENTER.lng,
        latitude: DEFAULT_CENTER.lat,
        zoom: DEFAULT_ZOOM,
      }
    }

    // Calculate bounds
    let minLng = Infinity,
      maxLng = -Infinity,
      minLat = Infinity,
      maxLat = -Infinity
    for (const coord of initialTrack) {
      minLng = Math.min(minLng, coord[0])
      maxLng = Math.max(maxLng, coord[0])
      minLat = Math.min(minLat, coord[1])
      maxLat = Math.max(maxLat, coord[1])
    }

    // Center and approximate zoom
    const centerLng = (minLng + maxLng) / 2
    const centerLat = (minLat + maxLat) / 2
    const lngSpan = maxLng - minLng
    const latSpan = maxLat - minLat
    const maxSpan = Math.max(lngSpan, latSpan)

    // Approximate zoom level based on span
    let zoom = 12
    if (maxSpan > 1) zoom = 7
    else if (maxSpan > 0.5) zoom = 8
    else if (maxSpan > 0.2) zoom = 9
    else if (maxSpan > 0.1) zoom = 10
    else if (maxSpan > 0.05) zoom = 11

    return {
      longitude: centerLng,
      latitude: centerLat,
      zoom,
    }
  }, [initialTrack])

  // Notify parent when route changes - pass the complete computed path
  useEffect(() => {
    if (!routeGeoJson || routeGeoJson.coordinates.length < 2) {
      onPointsChange([])
      return
    }
    const allPoints: GeoPoint[] = routeGeoJson.coordinates.map((coords) => ({
      lng: coords[0],
      lat: coords[0],
    }))
    onPointsChange(allPoints)
  }, [routeGeoJson, onPointsChange])

  // State for hover marker on route segments
  const [hoverPoint, setHoverPoint] = useState<{
    lng: number
    lat: number
    idx: number
  } | null>(null)

  // State for dragging a ghost point (before insertion)
  const [draggingGhost, setDraggingGhost] = useState<{
    lng: number
    lat: number
    idx: number
  } | null>(null)

  // Track which marker is being dragged and its current position
  const [draggingMarker, setDraggingMarker] = useState<{
    index: number
    lng: number
    lat: number
  } | null>(null)

  // Track current map zoom level
  const [currentZoom, setCurrentZoom] = useState(initialViewState.zoom)
  useEffect(() => updateZoom(currentZoom), [currentZoom, updateZoom])

  // Track Ctrl key state for direct line mode
  const ctrlKeyRef = useRef(false)
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Control') ctrlKeyRef.current = true
    }
    const handleKeyUp = (e: KeyboardEvent) => {
      if (e.key === 'Control') ctrlKeyRef.current = false
    }
    window.addEventListener('keydown', handleKeyDown)
    window.addEventListener('keyup', handleKeyUp)
    return () => {
      window.removeEventListener('keydown', handleKeyDown)
      window.removeEventListener('keyup', handleKeyUp)
    }
  }, [])

  const handleMarkerDragStart = useCallback(
    (index: number) => (event: MarkerDragEvent) => {
      setDraggingMarker({ index, lng: event.lngLat.lng, lat: event.lngLat.lat })
      setHoverPoint(null)
    },
    []
  )

  const handleMarkerDrag = useCallback(
    (index: number) => (event: MarkerDragEvent) => {
      setDraggingMarker((prev) =>
        prev && prev.index === index
          ? { index, lng: event.lngLat.lng, lat: event.lngLat.lat }
          : prev
      )
    },
    []
  )

  const handleMarkerDragEnd = useCallback(
    (index: number) => (event: MarkerDragEvent) => {
      updateControlPoint(
        index,
        { lng: event.lngLat.lng, lat: event.lngLat.lat },
        ctrlKeyRef.current
      )
      setDraggingMarker(null)
    },
    [updateControlPoint]
  )

  const handleMarkerRightClick = useCallback(
    (index: number) => (event: React.MouseEvent) => {
      event.preventDefault()
      removeControlPoint(index, ctrlKeyRef.current)
    },
    [removeControlPoint]
  )

  const handleMapClick = useCallback(
    (event: MapMouseEvent) => {
      if (hoverPoint) return
      addControlPoint({ lng: event.lngLat.lng, lat: event.lngLat.lat }, ctrlKeyRef.current)
    },
    [addControlPoint, hoverPoint]
  )

  const handleMouseMove = useCallback(
    (event: MapMouseEvent) => {
      if (draggingGhost) {
        setDraggingGhost((prev) =>
          prev ? { ...prev, lng: event.lngLat.lng, lat: event.lngLat.lat } : null
        )
        return
      }

      if (draggingMarker) return

      if (!mapRef.current || !routeGeoJson) return

      const MARKER_PROXIMITY_PX = 20
      for (const point of controlPoints) {
        const projected = mapRef.current.project([point.lng, point.lat])
        const dx = projected.x - event.point.x
        const dy = projected.y - event.point.y
        const distance = Math.sqrt(dx * dx + dy * dy)
        if (distance < MARKER_PROXIMITY_PX) {
          setHoverPoint(null)
          return
        }
      }

      const ROUTE_BUFFER_PX = 15
      const bbox: [[number, number], [number, number]] = [
        [event.point.x - ROUTE_BUFFER_PX, event.point.y - ROUTE_BUFFER_PX],
        [event.point.x + ROUTE_BUFFER_PX, event.point.y + ROUTE_BUFFER_PX],
      ]
      const features = mapRef.current.queryRenderedFeatures(bbox, {
        layers: ['route-line'],
      })

      if (features.length > 0) {
        const nearestIds = around(route.index, event.lngLat.lng, event.lngLat.lat, 1, 1)
        if (nearestIds.length === 1) {
          const nearestId = nearestIds[0]
          setHoverPoint({
            lng: route.points[nearestId].lng,
            lat: route.points[nearestId].lat,
            idx: nearestIds[0],
          })
          return
        }
      }

      setHoverPoint(null)
    },
    [route, routeGeoJson, draggingGhost, draggingMarker, controlPoints]
  )

  const handleMouseLeave = useCallback(() => {
    setHoverPoint(null)
  }, [])

  const handleMapMouseDown = useCallback(
    (event: MapMouseEvent) => {
      if (event.originalEvent.button !== 0) return
      if (!hoverPoint) return

      event.preventDefault()

      setDraggingGhost({
        lng: hoverPoint.lng,
        lat: hoverPoint.lat,
        idx: hoverPoint.idx,
      })
      setHoverPoint(null)
    },
    [hoverPoint]
  )

  const handleMouseUp = useCallback(() => {
    if (draggingGhost) {
      insertControlPoint(
        draggingGhost.idx,
        { lng: draggingGhost.lng, lat: draggingGhost.lat },
        ctrlKeyRef.current
      )
      setDraggingGhost(null)
    }
  }, [draggingGhost, insertControlPoint])

  const routeStats = {
    distance: 0,
    ascend: 0,
  }

  // Compute connection lines when dragging
  const dragConnectionLines = useMemo(() => {
    // Connection lines for dragging existing marker
    if (draggingMarker) {
      const { index, lng, lat } = draggingMarker
      const features: GeoJSON.Feature<GeoJSON.LineString>[] = []

      // Connect to previous point
      if (index > 0) {
        const prev = controlPoints[index - 1]
        features.push({
          type: 'Feature',
          geometry: {
            type: 'LineString',
            coordinates: [
              [prev.lng, prev.lat],
              [lng, lat],
            ],
          },
          properties: {},
        })
      }

      // Connect to next point
      if (index < controlPoints.length - 1) {
        const next = controlPoints[index + 1]
        features.push({
          type: 'Feature',
          geometry: {
            type: 'LineString',
            coordinates: [
              [lng, lat],
              [next.lng, next.lat],
            ],
          },
          properties: {},
        })
      }

      return {
        type: 'FeatureCollection' as const,
        features,
      }
    }

    // Connection lines for dragging ghost (new point being inserted)
    if (draggingGhost) {
      const { idx, lng, lat } = draggingGhost
      const features: GeoJSON.Feature<GeoJSON.LineString>[] = []

      const cpIdx = findPreviousControlPointIndex(idx, controlPoints)

      // Connect to point before insertion
      if (cpIdx < controlPoints.length) {
        const prev = controlPoints[cpIdx]
        features.push({
          type: 'Feature',
          geometry: {
            type: 'LineString',
            coordinates: [
              [prev.lng, prev.lat],
              [lng, lat],
            ],
          },
          properties: {},
        })
      }

      // Connect to point after insertion
      if (cpIdx + 1 < controlPoints.length) {
        const next = controlPoints[cpIdx + 1]
        features.push({
          type: 'Feature',
          geometry: {
            type: 'LineString',
            coordinates: [
              [lng, lat],
              [next.lng, next.lat],
            ],
          },
          properties: {},
        })
      }

      return {
        type: 'FeatureCollection' as const,
        features,
      }
    }

    return null
  }, [draggingMarker, draggingGhost, controlPoints])

  return (
    <div className="flex flex-col h-full">
      {/* Compact toolbar */}
      <div className="flex items-center justify-between px-3 py-2 bg-gray-50 border-b text-sm">
        <div className="flex items-center gap-3">
          {controlPoints.length > 0 && (
            <span className="text-gray-500">
              {t('pointCount', { count: controlPoints.length })}
            </span>
          )}
          {isLoading && (
            <span className="flex items-center text-indigo-600">
              <ArrowPathIcon className="w-4 h-4 mr-1 animate-spin" />
              {t('calculating')}
            </span>
          )}
          {error && <span className="text-red-600">{error}</span>}
          {routeStats && (
            <div className="flex items-center gap-2">
              <span className="font-medium text-gray-900">
                {routeStats.distance.toFixed(1)} {t('units.km')}
              </span>
              <span className="text-green-600">
                +{Math.round(routeStats.ascend)} {t('units.m')}
              </span>
            </div>
          )}
        </div>
        {controlPoints.length > 0 && (
          <button
            type="button"
            onClick={clearRoute}
            className="inline-flex items-center px-2 py-1 text-xs font-medium text-gray-600 hover:text-gray-900"
          >
            <TrashIcon className="w-3.5 h-3.5 mr-1" />
            {t('clear')}
          </button>
        )}
      </div>

      {/* Map */}
      <div className="relative flex-1">
        <Map
          ref={mapRef}
          mapLib={maplibregl}
          mapStyle={style}
          initialViewState={initialViewState}
          style={{ width: '100%', height: '100%' }}
          onClick={handleMapClick}
          onMouseDown={handleMapMouseDown}
          onMouseMove={handleMouseMove}
          onMouseLeave={handleMouseLeave}
          onMouseUp={handleMouseUp}
          interactiveLayerIds={routeGeoJson ? ['route-line'] : []}
          cursor={draggingGhost ? 'grabbing' : hoverPoint ? 'pointer' : 'crosshair'}
          onZoom={(e) => setCurrentZoom(e.viewState.zoom)}
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

          {/* Drag connection lines */}
          {dragConnectionLines && (
            <Source id="drag-connections" type="geojson" data={dragConnectionLines}>
              <Layer
                id="drag-connection-line"
                type="line"
                paint={{
                  'line-color': '#10B981',
                  'line-width': 3,
                  'line-dasharray': [4, 4],
                  'line-opacity': 1,
                }}
              />
            </Source>
          )}

          {/* Control point markers */}
          {controlPoints.map((point, index) => {
            const isFirst = index === 0
            const isLast = index === controlPoints.length - 1 && controlPoints.length > 1
            const isManual = point.manual

            let markerColor = 'bg-amber-500'
            if (isFirst) markerColor = 'bg-green-500'
            else if (isLast) markerColor = 'bg-red-500'
            else if (isManual) markerColor = 'bg-blue-500'

            // Use dragging position if this marker is being dragged
            const isDragging = draggingMarker?.index === index
            const lng = isDragging ? draggingMarker.lng : point.lng
            const lat = isDragging ? draggingMarker.lat : point.lat

            return (
              <Marker
                key={point.id}
                longitude={lng}
                latitude={lat}
                anchor="center"
                draggable
                onDragStart={handleMarkerDragStart(index)}
                onDrag={handleMarkerDrag(index)}
                onDragEnd={handleMarkerDragEnd(index)}
              >
                <div
                  className="flex items-center cursor-grab active:cursor-grabbing"
                  onContextMenu={handleMarkerRightClick(index)}
                >
                  <div
                    className={`w-6 h-6 ${markerColor} border-2 border-white rounded-full shadow-lg`}
                  />
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
