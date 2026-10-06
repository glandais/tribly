import { describe, expect, it } from 'vitest'
import { chainCandidate, geoJsonLatLon, zoneSource } from './useEventTimezone'

// docs/LEDGER_*.md API-60, plan §4: the first link with a point decides the chain.
const tokyo = { type: 'Point' as const, coordinates: [139.69, 35.68] }

describe('zone chains', () => {
  it('reads GeoJSON as [lon, lat]', () => {
    expect(geoJsonLatLon(tokyo)).toEqual({ lat: 35.68, lon: 139.69 })
    expect(geoJsonLatLon(undefined)).toBeNull()
  })

  it('skips absent links and links known to have no point', () => {
    const placeWithoutGeometry = zoneSource('place:1', { loaded: true, point: undefined })
    const deletedRoute = zoneSource('route:a', { loaded: false, failed: true })
    const route = zoneSource('route:b', { loaded: true, point: tokyo })
    expect(chainCandidate([undefined, placeWithoutGeometry, deletedRoute, route])).toEqual({
      key: 'route:b',
      point: { lat: 35.68, lon: 139.69 },
    })
  })

  it('stops at a link still loading, and falls back when nothing is left', () => {
    const loading = zoneSource('place:1', { loaded: false })
    const route = zoneSource('route:b', { loaded: true, point: tokyo })
    expect(chainCandidate([loading, route])).toEqual({ key: 'place:1', point: undefined })
    expect(chainCandidate([zoneSource(undefined, { loaded: false })])).toBeNull()
  })
})
