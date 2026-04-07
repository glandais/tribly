import type { LngLatBoundsLike } from 'maplibre-gl'
import type { ClimbDto, RouteDetailDto, TrackDto } from '@/api/dto'

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
export function getPointClimbGradient(point: number[], climbs: ClimbDto[]): number {
  for (const climb of climbs) {
    if (point[3] >= climb.startDistance && point[3] <= climb.endDistance) {
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
    const line: GeoJSON.Feature<GeoJSON.LineString> = {
      type: 'Feature',
      geometry: route.tracks[i].line,
      properties: { trackIndex: i, ...properties, climbs: route.tracks[i].climbs },
    }
    features.push(line)
  }
  return {
    type: 'FeatureCollection',
    features,
  }
}

// Calculate bounds from track points
// Note: MapLibre uses [lng, lat] order (GeoJSON standard)
export function calculateBounds(trackPoints: number[][]): LngLatBoundsLike {
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
    minLng = Math.min(minLng, point[0])
    maxLng = Math.max(maxLng, point[0])
    minLat = Math.min(minLat, point[1])
    maxLat = Math.max(maxLat, point[1])
  }

  return [
    [minLng, minLat],
    [maxLng, maxLat],
  ]
}

// Simple distance calculation (Haversine formula)
export function distance(lng1: number, lat1: number, lng2: number, lat2: number): number {
  const R = 6371000 // Earth radius in meters
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

// Returns km marker interval based on total distance
export function getKmMarkerInterval(distanceKm: number): number {
  if (distanceKm < 10) return 1
  if (distanceKm < 20) return 2
  if (distanceKm < 50) return 5
  return 10
}

// Compute km marker positions along coords.
// coords is [lng, lat, ele?, cumDistM?][]. If cumDistM (4th element) is present it is used
// directly; otherwise cumulative distance is computed using haversine.
export function computeKmMarkers(
  coords: number[][],
  totalDistanceM: number
): { lng: number; lat: number; label: string }[] {
  if (totalDistanceM <= 0 || coords.length < 2) return []

  const intervalM = getKmMarkerInterval(totalDistanceM / 1000) * 1000

  // Build cumulative-distance array: use 4th element when available, else compute
  const hasCumDist = coords[0].length >= 4 && coords[coords.length - 1][3] > 0
  const cumDists: number[] = hasCumDist
    ? coords.map((p) => p[3])
    : new Array<number>(coords.length).fill(0)

  if (!hasCumDist) {
    let acc = 0
    for (let i = 1; i < coords.length; i++) {
      acc += distance(coords[i - 1][0], coords[i - 1][1], coords[i][0], coords[i][1])
      cumDists[i] = acc
    }
  }

  const markers: { lng: number; lat: number; label: string }[] = []
  let targetDist = intervalM

  while (targetDist < totalDistanceM) {
    const idx = cumDists.findIndex((d) => d >= targetDist)
    if (idx > 0) {
      const p0 = coords[idx - 1]
      const p1 = coords[idx]
      const span = cumDists[idx] - cumDists[idx - 1]
      const t = span === 0 ? 0 : (targetDist - cumDists[idx - 1]) / span
      markers.push({
        lng: p0[0] + t * (p1[0] - p0[0]),
        lat: p0[1] + t * (p1[1] - p0[1]),
        label: String(Math.round(targetDist / 1000)),
      })
    }
    targetDist += intervalM
  }

  return markers
}

// Find nearest point to lat/lng
export function findNearestPoint(
  trackPoints: number[][],
  lat: number,
  lng: number,
  maxDistance: number
): number {
  let nearestIndex = -1
  let minDist = maxDistance

  for (let i = 0; i < trackPoints.length; i++) {
    const dist = distance(lng, lat, trackPoints[i][0], trackPoints[i][1])
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
    getPointClimbGradient(tracks[0].line.coordinates[0], tracks[0].climbs)
  )
  let currentSegment: [number, number][] = []

  for (let j = 0; j < tracks.length; j++) {
    const trackPoints = tracks[j].line.coordinates
    const climbs = tracks[j].climbs

    for (let i = 0; i < trackPoints.length; i++) {
      const point = trackPoints[i]
      const gradient = getPointClimbGradient(point, climbs)
      const color = getColorFromGradient(gradient)

      if (color !== currentColor && currentSegment.length > 0) {
        // Add last point of previous segment for continuity
        currentSegment.push([point[0], point[1]])

        features.push({
          type: 'Feature',
          properties: { color: currentColor },
          geometry: {
            type: 'LineString',
            coordinates: currentSegment,
          },
        })

        currentSegment = [[point[0], point[1]]]
        currentColor = color
      } else {
        currentSegment.push([point[0], point[1]])
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
