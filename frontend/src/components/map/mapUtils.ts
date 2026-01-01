import type { LngLatBoundsLike } from 'maplibre-gl'
import type { TrackPointDto, ClimbDto, RouteDetailDto, TrackDto } from '../../api/api'

// Color calculation (matching biketeam single-map.js)
const NEUTRAL_HUE = 210
const MIN_HUE = 85 // Green for low gradients
const MAX_HUE = 255 - 105 // Red for high gradients (360 - 105 = 255)
const SATURATION = '86%'
const LIGHTNESS = '62%'
export const NEUTRAL_COLOR = `hsl(${NEUTRAL_HUE},${SATURATION},${LIGHTNESS})`

// Calculate color based on gradient
export function getColorFromGradient(gradient: number): string {
  if (gradient === 0) return NEUTRAL_COLOR

  // Map gradient (0% to 18%) to hue (MIN_HUE to MAX_HUE)
  const normalizedGradient = Math.min(18, Math.max(0, gradient))
  let hue = Math.round(MIN_HUE + (normalizedGradient / 18.0) * (MAX_HUE - MIN_HUE))

  // Ensure hue is in valid range
  hue = Math.min(MIN_HUE, Math.max(MAX_HUE, hue))

  if (hue < 0) {
    hue = hue + 360
  }

  return `hsl(${hue},${SATURATION},${LIGHTNESS})`
}

// Determine if a point is in a climb and get its gradient
export function getPointClimbGradient(point: TrackPointDto, climbs: ClimbDto[]): number {
  for (const climb of climbs) {
    if (point.dist >= climb.startDistance && point.dist <= climb.endDistance) {
      return climb.averageGradient
    }
  }
  return 0
}

// Convert track points to GeoJSON LineString
export function routeToGeoJSON(
  route: RouteDetailDto,
  properties: Record<string, unknown> = {}
): GeoJSON.FeatureCollection<GeoJSON.LineString> {
  const features: GeoJSON.Feature<GeoJSON.LineString>[] = []
  for (let i = 0; i < route.tracks.length; i++) {
    const trackPoints = route.tracks[i].trackPoints
    features.push({
      type: 'Feature',
      geometry: {
        type: 'LineString',
        coordinates: trackPoints.map((p) => [p.lng, p.lat]),
      },
      properties: { segmentIndex: i, ...properties },
    })
  }
  return {
    type: 'FeatureCollection',
    features,
  }
}

// Calculate bounds from track points
// Note: MapLibre uses [lng, lat] order (GeoJSON standard)
export function calculateBounds(trackPoints: TrackPointDto[]): LngLatBoundsLike {
  if (trackPoints.length === 0) {
    return [
      [-180, -90],
      [180, 90],
    ]
  }

  let minLng = Infinity,
    maxLng = -Infinity
  let minLat = Infinity,
    maxLat = -Infinity

  for (const point of trackPoints) {
    minLng = Math.min(minLng, point.lng)
    maxLng = Math.max(maxLng, point.lng)
    minLat = Math.min(minLat, point.lat)
    maxLat = Math.max(maxLat, point.lat)
  }

  return [
    [minLng, minLat],
    [maxLng, maxLat],
  ]
}

// Simple distance calculation (Haversine formula)
export function distance(lng1: number, lat1: number, lng2: number, lat2: number): number {
  const R = 6371 // Earth radius in km
  const dLat = ((lat2 - lat1) * Math.PI) / 180
  const dLon = ((lng2 - lng1) * Math.PI) / 180
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos((lat1 * Math.PI) / 180) *
      Math.cos((lat2 * Math.PI) / 180) *
      Math.sin(dLon / 2) *
      Math.sin(dLon / 2)
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  return R * c
}

// Find nearest point to lat/lng
export function findNearestPoint(
  trackPoints: TrackPointDto[],
  lat: number,
  lng: number,
  maxDistance: number
): number {
  let nearestIndex = -1
  let minDist = maxDistance

  for (let i = 0; i < trackPoints.length; i++) {
    const dist = distance(lng, lat, trackPoints[i].lng, trackPoints[i].lat)
    if (dist < minDist) {
      minDist = dist
      nearestIndex = i
    }
  }

  return nearestIndex
}

// Create GeoJSON FeatureCollection with multiple line segments for gradient coloring
export function createGradientLineFeatures(
  tracks: TrackDto[]
): GeoJSON.FeatureCollection<GeoJSON.LineString> {
  const features: GeoJSON.Feature<GeoJSON.LineString>[] = []

  if (tracks.length === 0) {
    return { type: 'FeatureCollection', features }
  }

  let currentColor = getColorFromGradient(
    getPointClimbGradient(tracks[0].trackPoints[0], tracks[0].climbs)
  )
  let currentSegment: [number, number][] = []

  for (let j = 0; j < tracks.length; j++) {
    const trackPoints = tracks[j].trackPoints
    const climbs = tracks[j].climbs

    for (let i = 0; i < trackPoints.length; i++) {
      const point = trackPoints[i]
      const gradient = getPointClimbGradient(point, climbs)
      const color = getColorFromGradient(gradient)

      if (color !== currentColor && currentSegment.length > 0) {
        // Add last point of previous segment for continuity
        currentSegment.push([point.lng, point.lat])

        features.push({
          type: 'Feature',
          properties: { color: currentColor },
          geometry: {
            type: 'LineString',
            coordinates: currentSegment,
          },
        })

        currentSegment = [[point.lng, point.lat]]
        currentColor = color
      } else {
        currentSegment.push([point.lng, point.lat])
      }
    }

    // Add final segment
    if (currentSegment.length > 1) {
      features.push({
        type: 'Feature',
        properties: { color: currentColor },
        geometry: {
          type: 'LineString',
          coordinates: currentSegment,
        },
      })
    }
    currentSegment = []
  }

  return {
    type: 'FeatureCollection',
    features,
  }
}
