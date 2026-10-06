import { useMemo, useState } from 'react'
import { useQueries } from '@tanstack/react-query'
import { useDebouncedValue } from '@mantine/hooks'
import { getGetTeamTimezoneQueryOptions } from '@/api/endpoints/teams/teams'
import type { GeoJsonPoint } from '@/api/dto'

/**
 * The zone an event form's wall times are read in, while it is being edited (docs/LEDGER_*.md
 * API-60, plan §4 and §6) — for the « heure de Tokyo » mention only: the form sends wall times and
 * the backend resolves the zone itself, so a stale guess here mislabels, it never shifts a time.
 *
 * The backend's chains (`EventTimezoneResolver`) are mirrored as lists of {@link ZoneSource}, one per
 * link: a ride is its start place, then its route; a stage its start place, then its route, then
 * the previous stage, the trip's route and the team. The first link that has a point decides.
 */

/** A point, as `GET /api/teams/{teamSlug}/timezone` takes it. */
export interface LatLon {
  lat: number
  lon: number
}

/**
 * One link of a chain: a start place or a route, named by `key` (`place:<id>`, `route:<slug>`).
 * `point` is `undefined` while its data loads, `null` once known to have none (a place without
 * geometry, a route without a start, a deleted route) — the chain then moves on.
 */
export interface ZoneSource {
  key: string
  point: LatLon | null | undefined
}

/** A GeoJSON `[lon, lat]` point as a {@link LatLon}. */
export function geoJsonLatLon(point: GeoJsonPoint | null | undefined): LatLon | null {
  const [lon, lat] = point?.coordinates ?? []
  return typeof lat === 'number' && typeof lon === 'number' ? { lat, lon } : null
}

/**
 * A link for an entity looked up by query: absent when there is no reference, its point once
 * loaded, `null` when the lookup failed (a deleted route is skipped by the backend too).
 */
export function zoneSource(
  key: string | undefined,
  lookup: { loaded: boolean; point?: GeoJsonPoint | null; failed?: boolean }
): ZoneSource | undefined {
  if (!key) return undefined
  if (lookup.loaded) return { key, point: geoJsonLatLon(lookup.point) }
  return { key, point: lookup.failed ? null : undefined }
}

/**
 * The link that decides a chain: the first present one not known to lack a point — possibly still
 * loading. `null` when every link is absent or pointless: the caller falls back (previous stage,
 * team).
 */
export function chainCandidate(chain: (ZoneSource | undefined)[]): ZoneSource | null {
  for (const source of chain) {
    if (source && source.point !== null) return source
  }
  return null
}

// Rounded to ~100 m: a zone border is never that fine, and it keeps the query key stable.
function pointKey({ lat, lon }: LatLon): string {
  return `${lat.toFixed(3)},${lon.toFixed(3)}`
}

/** `undefined`: not known yet (loading, debouncing). `null`: no candidate, fall back. */
export type ZoneResolution = string | null | undefined

/**
 * Resolves chain candidates to zones. `seeds` maps a source key to the zone the API already gave
 * for it (an existing entity's `timezone`): an untouched form makes no call, and the call only
 * fires once the organiser picks another place or route. Every other candidate's point goes through
 * `GET /api/teams/{teamSlug}/timezone`, debounced, one query per distinct point — React Query
 * dedups, and a point already asked stays known for the session.
 */
export function useZoneResolver(
  teamSlug: string,
  candidates: (ZoneSource | null)[],
  seeds: Readonly<Record<string, string>>
): (candidate: ZoneSource | null) => ZoneResolution {
  const wanted = Array.from(
    new Set(
      candidates
        .filter((c): c is ZoneSource => !!c && !seeds[c.key] && !!c.point)
        .map((c) => pointKey(c.point!))
    )
  )
    .sort()
    .join('|')
  const [debounced] = useDebouncedValue(wanted, 400)
  const keys = useMemo(() => (debounced ? debounced.split('|') : []), [debounced])
  const results = useQueries({
    queries: keys.map((key) => {
      const [lat, lon] = key.split(',').map(Number)
      return {
        ...getGetTeamTimezoneQueryOptions(teamSlug, { lat, lon }),
        enabled: !!teamSlug,
        staleTime: Infinity,
        retry: false,
      }
    }),
  })
  const zones = new Map<string, string>()
  keys.forEach((key, index) => {
    const zone = results[index]?.data?.timezone
    if (zone) zones.set(key, zone)
  })
  return (candidate) => {
    if (!candidate) return null
    const seed = seeds[candidate.key]
    if (seed) return seed
    if (!candidate.point) return undefined
    return zones.get(pointKey(candidate.point))
  }
}

/**
 * `value` once known, else the last value that was: a zone being looked up keeps its previous
 * mention rather than flickering to the fallback.
 */
export function useLastKnown<T extends string>(value: T | undefined, initial: T): T {
  const [last, setLast] = useState<T>(value ?? initial)
  if (value !== undefined && value !== last) setLast(value)
  return value ?? last
}
