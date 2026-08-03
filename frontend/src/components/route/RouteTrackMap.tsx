import { useCallback, useEffect, useMemo } from 'react'
import { Source, Layer, MapRef, MapMouseEvent } from 'react-map-gl/maplibre'
import type { PaddingOptions } from 'maplibre-gl'
import { Box } from '@mantine/core'
import {
  StartMarker,
  EndMarker,
  HoverMarker,
  WaypointMarker,
  KmMarkersLayer,
} from '../map/MapMarkers'
import {
  calculateBounds,
  distance,
  findNearestPoint,
  createGradientLineFeatures,
} from '../map/mapUtils'
import { PedalonsMap } from '../map/PedalonsMap'
import { useHideTrackKey } from '@/hooks/useHideTrackKey'
import { useResolvedColorScheme } from '@/hooks/useResolvedColorScheme'
import type { MappableRoute } from './RouteMapView'
// maplibre-gl CSS is provided by maplibre-theme in index.css

interface RouteTrackMapProps {
  route: MappableRoute
  mapRef: React.RefObject<MapRef | null>
  hoveredPointIndex: number
  onHoverPoint: (index: number) => void
  /** Padding used when fitting the track to the viewport. Defaults to 50px on every side. */
  fitPadding?: number | PaddingOptions
  onMoveStart?: () => void
  onMoveEnd?: () => void
  /** Overlays (chart, action buttons) absolutely positioned on top of the map. */
  children?: React.ReactNode
}

/**
 * The gradient-colored track map extracted from RouteMapView: MapLibre map + colored line,
 * start/end/waypoint markers, km markers, hover marker, and nearest-point hover handling.
 * Fills its parent (100% width/height); the parent decides the actual size.
 */
export function RouteTrackMap({
  route,
  mapRef,
  hoveredPointIndex,
  onHoverPoint,
  fitPadding = 50,
  onMoveStart,
  onMoveEnd,
  children,
}: RouteTrackMapProps) {
  const colorScheme = useResolvedColorScheme()

  // Press "h" to hide the trace and its markers, revealing the basemap underneath
  const trackHidden = useHideTrackKey()

  // Flatten all track points from multiple tracks
  const trackPoints = useMemo(
    () => route.tracks.flatMap((track) => track.line.coordinates) || [],
    [route.tracks]
  )
  const waypoints = route.waypoints || []

  // Create gradient line GeoJSON
  const lineFeatures = useMemo(() => createGradientLineFeatures(route.tracks), [route.tracks])

  const fitToBounds = useCallback(() => {
    if (mapRef.current && trackPoints.length > 0) {
      const bounds = calculateBounds(trackPoints)
      mapRef.current.fitBounds(bounds, {
        padding: fitPadding,
        duration: 300,
      })
    }
  }, [trackPoints, fitPadding, mapRef])

  // Fit bounds when map is first loaded
  const handleMapLoad = useCallback(() => {
    fitToBounds()
  }, [fitToBounds])

  // Re-center when route changes (e.g. switching trip stages)
  useEffect(() => {
    fitToBounds()
  }, [fitToBounds])

  // Handle mouse move for nearest point detection
  const handleMouseMove = useCallback(
    (event: MapMouseEvent) => {
      if (!mapRef.current || trackPoints.length === 0) return

      const bounds = mapRef.current.getBounds()
      const sw = bounds.getSouthWest()
      const ne = bounds.getNorthEast()
      const mapViewSize = distance(sw.lng, sw.lat, ne.lng, ne.lat)

      const nearestIndex = findNearestPoint(
        trackPoints,
        event.lngLat.lat,
        event.lngLat.lng,
        mapViewSize / 20.0
      )

      if (nearestIndex >= 0) {
        onHoverPoint(nearestIndex)
      }
    },
    [trackPoints, mapRef, onHoverPoint]
  )

  const handleMouseLeave = useCallback(() => {
    onHoverPoint(-1)
  }, [onHoverPoint])

  const hoveredPoint = hoveredPointIndex >= 0 ? trackPoints[hoveredPointIndex] : null

  return (
    <Box
      pos="relative"
      w="100%"
      h="100%"
      className={colorScheme === 'dark' ? 'dark' : undefined}
      style={{ zIndex: 0 }}
    >
      <PedalonsMap
        ref={mapRef}
        mapStyleSwitcherPosition="top-right"
        initialViewState={{
          longitude: trackPoints[0][0],
          latitude: trackPoints[0][1],
          zoom: 11,
        }}
        onLoad={handleMapLoad}
        onMouseMove={handleMouseMove}
        onMouseLeave={handleMouseLeave}
        onMoveStart={onMoveStart}
        onMoveEnd={onMoveEnd}
      >
        {!trackHidden && (
          <>
            {/* Gradient-colored route line */}
            <Source id="route-segments" type="geojson" data={lineFeatures}>
              <Layer
                id="route-line"
                type="line"
                paint={{
                  'line-color': ['get', 'color'],
                  'line-width': 8,
                  'line-opacity': 0.8,
                }}
              />
            </Source>

            {/* Start marker */}
            <StartMarker longitude={trackPoints[0][0]} latitude={trackPoints[0][1]} />

            {/* End marker */}
            <EndMarker
              longitude={trackPoints[trackPoints.length - 1][0]}
              latitude={trackPoints[trackPoints.length - 1][1]}
            />

            {/* Waypoints */}
            {waypoints.map(
              (waypoint, index) =>
                waypoint.geometry.coordinates[0] &&
                waypoint.geometry.coordinates[1] && (
                  <WaypointMarker
                    key={`waypoint-${index}`}
                    longitude={waypoint.geometry.coordinates[0]}
                    latitude={waypoint.geometry.coordinates[1]}
                    name={waypoint.name}
                  />
                )
            )}

            {/* Km markers */}
            <KmMarkersLayer coords={trackPoints} totalDistanceM={route.distance} />

            {/* Hover marker */}
            {hoveredPoint && <HoverMarker longitude={hoveredPoint[0]} latitude={hoveredPoint[1]} />}
          </>
        )}
      </PedalonsMap>

      {children}
    </Box>
  )
}
