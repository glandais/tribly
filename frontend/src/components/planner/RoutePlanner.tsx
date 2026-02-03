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
import { IconTrash } from '@tabler/icons-react'
import { ActionIcon, Box, Group, Loader, Stack, Text, useComputedColorScheme } from '@mantine/core'
import { MapStyleSwitcher } from '../map/MapStyleSwitcher'
import { UndoRedoControl } from './UndoRedoControl'
import { useMapStyle } from '../../hooks/useMapStyle'
import { useUnits } from '../../hooks/useUnits'
import { useRoutePlanner, findBboxStartPoint, findBboxEndPoint } from '../../hooks/useRoutePlanner'
import type { GeoPoint } from '@/api/dto'
// maplibre-gl CSS is provided by maplibre-theme in index.css
import { RoutePoint } from '@/lib/planner'
import { getOverlayBg } from '@/lib/colors'
import { around } from 'geokdbush'

// Default center (France)
const DEFAULT_CENTER = { lng: -1.55, lat: 47.22 }
const DEFAULT_ZOOM = 12

interface RoutePlannerProps {
  onPointsChange: (points: GeoPoint[]) => void
  initialTrack?: number[][] // [lng, lat, ele, dist][] from existing route
  teamLocation?: [number, number] // [lng, lat] from team geometry
}

export function RoutePlanner({ onPointsChange, initialTrack, teamLocation }: RoutePlannerProps) {
  const { t } = useTranslation()
  const { distance, elevation } = useUnits()
  const colorScheme = useComputedColorScheme('light')
  const { styleId, setStyleId, style } = useMapStyle()
  const mapRef = useRef<MapRef>(null)
  const mapContainerRef = useRef<HTMLDivElement>(null)

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
    clearRoute,
    canUndo,
    canRedo,
    undo,
    redo,
  } = useRoutePlanner({ initialTrack })

  const getMapBounds = useCallback(() => mapRef.current?.getBounds() ?? null, [])

  // Calculate initial view state from track bounds or team location
  const initialViewState = useMemo(() => {
    if (!initialTrack || initialTrack.length === 0) {
      // Use team location if available, otherwise fall back to default
      const center = teamLocation ? { lng: teamLocation[0], lat: teamLocation[1] } : DEFAULT_CENTER
      return {
        longitude: center.lng,
        latitude: center.lat,
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
  }, [initialTrack, teamLocation])

  // Notify parent when route changes - pass the complete computed path
  useEffect(() => {
    if (!routeGeoJson || routeGeoJson.coordinates.length < 2) {
      onPointsChange([])
      return
    }
    const allPoints: GeoPoint[] = routeGeoJson.coordinates.map((coords) => ({
      lng: coords[0],
      lat: coords[1],
    }))
    onPointsChange(allPoints)
  }, [routeGeoJson, onPointsChange])

  // State for effective start on edition
  const [startDragPoint, setStartDragPoint] = useState<RoutePoint | undefined>(undefined)

  // State for effective end on edition
  const [endDragPoint, setEndDragPoint] = useState<RoutePoint | undefined>(undefined)

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
    lng: number
    lat: number
    idx: number
  } | null>(null)

  // Touch device detection
  const [isTouchDevice] = useState(() => 'ontouchstart' in window)

  // Long-press context menu state
  const [contextMenu, setContextMenu] = useState<{
    x: number
    y: number
    idx: number
  } | null>(null)
  const longPressTimer = useRef<ReturnType<typeof setTimeout> | null>(null)
  const longPressTouchStart = useRef<{ x: number; y: number } | null>(null)

  const clearLongPress = useCallback(() => {
    if (longPressTimer.current) {
      clearTimeout(longPressTimer.current)
      longPressTimer.current = null
    }
    longPressTouchStart.current = null
  }, [])

  const handleMarkerTouchStart = useCallback(
    (idx: number) => (event: React.TouchEvent) => {
      const touch = event.touches[0]
      longPressTouchStart.current = { x: touch.clientX, y: touch.clientY }
      longPressTimer.current = setTimeout(() => {
        const rect = mapContainerRef.current?.getBoundingClientRect()
        const x = rect ? touch.clientX - rect.left : touch.clientX
        const y = rect ? touch.clientY - rect.top : touch.clientY
        setContextMenu({ x, y, idx })
        longPressTimer.current = null
      }, 500)
    },
    []
  )

  const handleMarkerTouchMove = useCallback(
    (event: React.TouchEvent) => {
      if (!longPressTouchStart.current) return
      const touch = event.touches[0]
      const dx = touch.clientX - longPressTouchStart.current.x
      const dy = touch.clientY - longPressTouchStart.current.y
      if (Math.sqrt(dx * dx + dy * dy) > 10) {
        clearLongPress()
      }
    },
    [clearLongPress]
  )

  const handleMarkerTouchEnd = useCallback(() => {
    clearLongPress()
  }, [clearLongPress])

  const handleContextMenuDelete = useCallback(() => {
    if (!contextMenu) return
    removeControlPoint(contextMenu.idx, ctrlKeyRef.current, getMapBounds())
    setContextMenu(null)
  }, [contextMenu, removeControlPoint, getMapBounds])

  // Track Ctrl key state for direct line mode + undo/redo shortcuts
  const ctrlKeyRef = useRef(false)
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Control') ctrlKeyRef.current = true
      if ((e.ctrlKey || e.metaKey) && e.key === 'z' && !e.shiftKey) {
        e.preventDefault()
        undo()
      }
      if ((e.ctrlKey || e.metaKey) && e.key === 'z' && e.shiftKey) {
        e.preventDefault()
        redo()
      }
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
  }, [undo, redo])

  const handleMarkerDragStart = useCallback(
    (idx: number) => (event: MarkerDragEvent) => {
      setDraggingMarker({ idx, lng: event.lngLat.lng, lat: event.lngLat.lat })
      const bounds = getMapBounds()
      setStartDragPoint(findBboxStartPoint(route, idx, bounds))
      setEndDragPoint(findBboxEndPoint(route, idx, bounds))
      setHoverPoint(null)
    },
    [route, getMapBounds]
  )

  const handleMarkerDrag = useCallback(
    (idx: number) => (event: MarkerDragEvent) => {
      setDraggingMarker((prev) =>
        prev && prev.idx === idx ? { idx, lng: event.lngLat.lng, lat: event.lngLat.lat } : prev
      )
    },
    []
  )

  const handleMarkerDragEnd = useCallback(
    () => (event: MarkerDragEvent) => {
      updateControlPoint(
        startDragPoint,
        { lng: event.lngLat.lng, lat: event.lngLat.lat },
        endDragPoint,
        ctrlKeyRef.current
      )
      setDraggingMarker(null)
      setStartDragPoint(undefined)
      setEndDragPoint(undefined)
    },
    [updateControlPoint, startDragPoint, endDragPoint]
  )

  const handleMarkerRightClick = useCallback(
    (index: number) => (event: React.MouseEvent) => {
      event.preventDefault()
      removeControlPoint(index, ctrlKeyRef.current, getMapBounds())
    },
    [removeControlPoint, getMapBounds]
  )

  const handleMapClick = useCallback(
    (event: MapMouseEvent) => {
      // Close context menu on any map click
      if (contextMenu) {
        setContextMenu(null)
        return
      }

      if (hoverPoint) return

      // On touch: check if tap is near the route line for insertion
      if (isTouchDevice && mapRef.current && routeGeoJson && route.points.length > 0) {
        const TOUCH_BUFFER_PX = 25
        const bbox: [[number, number], [number, number]] = [
          [event.point.x - TOUCH_BUFFER_PX, event.point.y - TOUCH_BUFFER_PX],
          [event.point.x + TOUCH_BUFFER_PX, event.point.y + TOUCH_BUFFER_PX],
        ]
        const features = mapRef.current.queryRenderedFeatures(bbox, {
          layers: ['route-line'],
        })
        if (features.length > 0) {
          const nearestIds = around(route.index, event.lngLat.lng, event.lngLat.lat, 1, 1)
          if (nearestIds.length === 1) {
            const nearestId = nearestIds[0]
            const bounds = getMapBounds()
            const start = findBboxStartPoint(route, nearestId, bounds)
            const end = findBboxEndPoint(route, nearestId, bounds)
            insertControlPoint(start, { lng: event.lngLat.lng, lat: event.lngLat.lat }, end, false)
            return
          }
        }
      }

      addControlPoint({ lng: event.lngLat.lng, lat: event.lngLat.lat }, ctrlKeyRef.current)
    },
    [
      addControlPoint,
      hoverPoint,
      contextMenu,
      routeGeoJson,
      route,
      insertControlPoint,
      getMapBounds,
      isTouchDevice,
    ]
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
      const bounds = getMapBounds()
      setStartDragPoint(findBboxStartPoint(route, hoverPoint.idx, bounds))
      setEndDragPoint(findBboxEndPoint(route, hoverPoint.idx, bounds))
      setHoverPoint(null)
    },
    [hoverPoint, getMapBounds, route]
  )

  const handleMouseUp = useCallback(() => {
    if (draggingGhost) {
      insertControlPoint(
        startDragPoint,
        { lng: draggingGhost.lng, lat: draggingGhost.lat },
        endDragPoint,
        ctrlKeyRef.current
      )
      setDraggingGhost(null)
      setStartDragPoint(undefined)
      setEndDragPoint(undefined)
    }
  }, [insertControlPoint, draggingGhost, startDragPoint, endDragPoint])

  const routeStats = {
    distance: 0,
    ascend: 0,
  }

  // Compute connection lines and boundary points when dragging
  const { dragConnectionLines } = useMemo(() => {
    // Connection lines for dragging existing marker
    if (draggingMarker || draggingGhost) {
      const { lng, lat } = (draggingMarker || draggingGhost)!
      const features: GeoJSON.Feature<GeoJSON.LineString>[] = []

      if (startDragPoint) {
        features.push({
          type: 'Feature',
          geometry: {
            type: 'LineString',
            coordinates: [
              [startDragPoint.lng, startDragPoint.lat],
              [lng, lat],
            ],
          },
          properties: {},
        })
      }

      if (endDragPoint) {
        features.push({
          type: 'Feature',
          geometry: {
            type: 'LineString',
            coordinates: [
              [lng, lat],
              [endDragPoint.lng, endDragPoint.lat],
            ],
          },
          properties: {},
        })
      }

      return {
        dragConnectionLines: { type: 'FeatureCollection' as const, features },
      }
    }

    return { dragConnectionLines: null }
  }, [draggingMarker, draggingGhost, startDragPoint, endDragPoint])

  return (
    <Stack gap={0} h="100%">
      {/* Compact toolbar */}
      <Group
        justify="space-between"
        px="sm"
        py="xs"
        bg="var(--mantine-color-body)"
        style={{ borderBottom: '1px solid var(--mantine-color-default-border)' }}
      >
        <Group>
          {controlPoints.length > 0 && (
            <Text size="sm" c="dimmed">
              {t('planner.pointCount', { count: controlPoints.length })}
            </Text>
          )}
          {isLoading && (
            <Group gap="xs" c="primary">
              <Loader size="xs" />
              <Text size="sm">{t('planner.calculating')}</Text>
            </Group>
          )}
          {error && (
            <Text size="sm" c="red">
              {error}
            </Text>
          )}
          {routeStats && (
            <Group gap="xs">
              <Text size="sm" fw={500}>
                {distance(routeStats.distance)}
              </Text>
              <Text size="sm" c="green">
                +{elevation(routeStats.ascend)}
              </Text>
            </Group>
          )}
        </Group>
        {controlPoints.length > 0 && (
          <ActionIcon variant="subtle" color="gray" onClick={clearRoute} title={t('planner.clear')}>
            <IconTrash size={14} />
          </ActionIcon>
        )}
      </Group>

      {/* Map */}
      <Box
        ref={mapContainerRef}
        pos="relative"
        className={colorScheme === 'dark' ? 'dark' : undefined}
        style={{ flex: 1 }}
      >
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
        >
          <NavigationControl position="top-left" />
          <UndoRedoControl
            position="top-left"
            canUndo={canUndo}
            canRedo={canRedo}
            onUndo={undo}
            onRedo={redo}
          />
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

            let markerColor = 'var(--mantine-color-blue-filled)'
            if (isFirst) markerColor = 'var(--mantine-color-green-filled)'
            else if (isLast) markerColor = 'var(--mantine-color-red-filled)'

            // Use dragging position if this marker is being dragged
            const isDragging = draggingMarker?.idx === point.idx
            const lng = isDragging ? draggingMarker.lng : point.lng
            const lat = isDragging ? draggingMarker.lat : point.lat

            return (
              <Marker
                key={point.id}
                longitude={lng}
                latitude={lat}
                anchor="center"
                draggable
                onDragStart={handleMarkerDragStart(point.idx)}
                onDrag={handleMarkerDrag(point.idx)}
                onDragEnd={handleMarkerDragEnd()}
              >
                <Box
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    cursor: 'grab',
                    width: 44,
                    height: 44,
                    pointerEvents: 'auto',
                  }}
                  onContextMenu={handleMarkerRightClick(point.idx)}
                  onTouchStart={handleMarkerTouchStart(point.idx)}
                  onTouchMove={handleMarkerTouchMove}
                  onTouchEnd={handleMarkerTouchEnd}
                >
                  <Box
                    w={24}
                    h={24}
                    style={{
                      backgroundColor: markerColor,
                      border: '2px solid white',
                      borderRadius: '50%',
                      boxShadow: 'var(--mantine-shadow-lg)',
                    }}
                  />
                </Box>
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
              <Box
                w={20}
                h={20}
                style={{
                  backgroundColor: draggingGhost
                    ? 'var(--mantine-primary-color-filled)'
                    : 'var(--mantine-primary-color-light-color)',
                  border: '2px solid white',
                  borderRadius: '50%',
                  boxShadow: 'var(--mantine-shadow-lg)',
                  opacity: draggingGhost ? 1 : 0.7,
                  pointerEvents: 'none',
                  transition: 'opacity 150ms',
                }}
              />
            </Marker>
          )}

          {/* Bbox boundary markers showing effective start/end during drag */}
          {startDragPoint && (
            <Marker
              key={`bbox-boundary-start`}
              longitude={startDragPoint.lng}
              latitude={startDragPoint.lat}
              anchor="center"
            >
              <Box
                w={14}
                h={14}
                style={{
                  backgroundColor: '#10B981',
                  border: '2px solid white',
                  borderRadius: '50%',
                  boxShadow: 'var(--mantine-shadow-sm)',
                  pointerEvents: 'none',
                }}
              />
            </Marker>
          )}
          {endDragPoint && (
            <Marker
              key={`bbox-boundary-end`}
              longitude={endDragPoint.lng}
              latitude={endDragPoint.lat}
              anchor="center"
            >
              <Box
                w={14}
                h={14}
                style={{
                  backgroundColor: '#10B981',
                  border: '2px solid white',
                  borderRadius: '50%',
                  boxShadow: 'var(--mantine-shadow-sm)',
                  pointerEvents: 'none',
                }}
              />
            </Marker>
          )}
        </Map>

        {/* Long-press context menu for touch devices */}
        {contextMenu && (
          <Box
            pos="absolute"
            px="xs"
            py={4}
            bg="var(--mantine-color-body)"
            style={{
              left: contextMenu.x,
              top: contextMenu.y,
              transform: 'translate(-50%, -120%)',
              zIndex: 10,
              borderRadius: 'var(--mantine-radius-md)',
              boxShadow: 'var(--mantine-shadow-md)',
              border: '1px solid var(--mantine-color-default-border)',
            }}
          >
            <Group
              gap="xs"
              style={{ cursor: 'pointer' }}
              c="red"
              onClick={handleContextMenuDelete}
              px="xs"
              py={6}
            >
              <IconTrash size={16} />
              <Text size="sm">{t('planner.menu.delete')}</Text>
            </Group>
          </Box>
        )}

        {/* Instructions overlay */}
        {controlPoints.length === 0 && (
          <Box
            pos="absolute"
            bottom={16}
            left="50%"
            px="md"
            py="xs"
            bg={getOverlayBg(colorScheme)}
            style={{
              transform: 'translateX(-50%)',
              borderRadius: 'var(--mantine-radius-md)',
              boxShadow: 'var(--mantine-shadow-lg)',
            }}
          >
            <Text size="sm" c="dimmed">
              {isTouchDevice ? t('planner.instructions.touch') : t('planner.instructions')}
            </Text>
          </Box>
        )}
      </Box>
    </Stack>
  )
}
