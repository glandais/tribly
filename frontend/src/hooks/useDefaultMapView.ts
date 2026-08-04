import { useMemo } from 'react'
import { useGetConfig } from '@/api/endpoints/configuration/configuration'

export interface DefaultMapView {
  longitude: number
  latitude: number
  zoom: number
}

/**
 * Where a map opens when nothing else frames it — the pinned team's location, or the deployment
 * centre, as `GET /api/config` resolves it. `null` until that answer lands: a caller must then hold
 * the map back rather than open on a compiled-in guess, which is what used to make every map flash
 * through metropolitan France (or Nantes, in the planner) before snapping to its real extent.
 *
 * Same React Query entry as {@link useMapStyle}, so this costs no extra request, and `/api/config`
 * is prefetched during SSR — on a public page the value is there on the first render.
 *
 * There is deliberately no compiled-in fallback, for the same reason the basemap list has none:
 * without the config `PedalonsMap` has no `mapStyle` either and would paint an empty canvas, so
 * `null` is the honest signal that there is nothing to open yet.
 */
export function useDefaultMapView(): DefaultMapView | null {
  const { data: config } = useGetConfig()
  const center = config?.defaultCenter

  return useMemo(
    () => (center ? { longitude: center.lon, latitude: center.lat, zoom: center.zoom } : null),
    [center]
  )
}
