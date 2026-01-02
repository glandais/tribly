const BROUTER_URL = 'http://localhost:17777'

export interface ControlPoint {
  id: string
  lng: number
  lat: number
}

let pointIdCounter = 0

export function createControlPoint(lng: number, lat: number): ControlPoint {
  return {
    id: `cp-${++pointIdCounter}`,
    lng,
    lat,
  }
}

/**
 * Calculate a route segment between exactly 2 points
 */
export async function calculateSegment(
  from: ControlPoint,
  to: ControlPoint
): Promise<GeoJSON.FeatureCollection> {
  const lonlats = `${from.lng},${from.lat}|${to.lng},${to.lat}`
  const url = `${BROUTER_URL}/?lonlats=${lonlats}&profile=fastbike&format=geojson&alternativeidx=0`

  const response = await fetch(url)

  if (!response.ok) {
    throw new Error(`BRouter request failed: ${response.status}`)
  }

  return response.json()
}
