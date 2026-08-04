import { useMemo } from 'react'
import { useQueries, keepPreviousData } from '@tanstack/react-query'
import { getGetRoutesBulkQueryOptions } from '@/api/endpoints/routes/routes'
import type { RouteDetailDto, BoundsDto, GetRoutesBulkParams } from '@/api/dto'

/**
 * Mirrors the backend's `RouteService.MAX_BULK_SLUGS` — the most `slug` params a single
 * `/routes/bulk` call accepts before the endpoint returns 400. Every caller that builds a slug
 * list from an unbounded collection (ride groups, trip stages) must chunk against this constant
 * — see `useRoutesBulk`, which does it for you.
 */
export const ROUTES_BULK_MAX_SLUGS = 50

export interface RoutesBulkResult {
  routes: RouteDetailDto[]
  extent?: BoundsDto
}

function chunk<T>(items: readonly T[], size: number): T[][] {
  if (items.length === 0) return []
  const chunks: T[][] = []
  for (let i = 0; i < items.length; i += size) {
    chunks.push(items.slice(i, i + size))
  }
  return chunks
}

function mergeExtents(extents: (BoundsDto | undefined)[]): BoundsDto | undefined {
  return extents.reduce<BoundsDto | undefined>((acc, extent) => {
    if (!extent) return acc
    if (!acc) return extent
    return {
      minLon: Math.min(acc.minLon, extent.minLon),
      minLat: Math.min(acc.minLat, extent.minLat),
      maxLon: Math.max(acc.maxLon, extent.maxLon),
      maxLat: Math.max(acc.maxLat, extent.maxLat),
    }
  }, undefined)
}

/**
 * Wraps `useGetRoutesBulk` to survive slug lists bigger than the backend's per-call cap
 * (`ROUTES_BULK_MAX_SLUGS`): a 55-stage trip used to fire one oversized request, get a single 400
 * back, and blank its whole map/preview instead of degrading. This splits `params.slug` into
 * chunks of at most `ROUTES_BULK_MAX_SLUGS` and issues one bulk query per chunk via `useQueries`,
 * merging the routes and extents back into one result. The overwhelmingly common case is a single
 * chunk, so this adds no extra request for a normal ride or trip.
 *
 * Merging is best-effort: `data` is built from whichever chunks *did* resolve, so if e.g. the
 * second of three chunks 500s, the routes from the first and third still render — only the slugs
 * that were in the failing chunk are missing from `data.routes`. `extent` is likewise the union of
 * the extents of the chunks that resolved (`undefined` only if none did). `failedSlugs` names the
 * slugs whose chunk errored, so a caller rendering one row per slug can tell "still loading" (slug
 * absent from both `routes` and `failedSlugs`) apart from "this slug failed" (slug in
 * `failedSlugs`) instead of only having the aggregate `isLoading`/`isFetching`.
 *
 * Every chunk query uses `placeholderData: keepPreviousData`, so when the slug set changes (e.g.
 * picking a different route for one stage) already-resolved routes keep rendering while only the
 * affected chunk(s) refetch. Callers that render one row per slug should key that row's loading
 * state off whether its own slug is present in the merged `routes` or `failedSlugs` (plus
 * `isLoading`/`isFetching` for the "still loading" case) rather than off the aggregate `isLoading`
 * alone — see `RoutePreviewCompact` call sites.
 */
export function useRoutesBulk(
  teamSlug: string,
  params: Omit<GetRoutesBulkParams, 'slug'> & { slug: string[] },
  options?: { enabled?: boolean }
) {
  const { slug: slugs, ...restParams } = params
  const slugChunks = useMemo(() => chunk(slugs, ROUTES_BULK_MAX_SLUGS), [slugs])
  const enabled = (options?.enabled ?? true) && slugChunks.length > 0

  // Merging happens inside `combine` rather than in the component body so `data` keeps a stable
  // identity across renders that didn't change any chunk's result. Built inline, it was a fresh
  // object every render, and every consumer memo keyed on it (route lists, map bounds) re-ran —
  // `RoutesMapView` re-framed its map on each parent re-render, throwing away the user's zoom.
  const { data, isLoading, isFetching, error, failedChunks } = useQueries({
    queries: slugChunks.map((slugChunk) => {
      const chunkParams: GetRoutesBulkParams = { ...restParams, slug: slugChunk }
      return {
        ...getGetRoutesBulkQueryOptions(teamSlug, chunkParams),
        enabled,
        placeholderData: keepPreviousData,
      }
    }),
    combine: (results) => ({
      data:
        results.length > 0
          ? ({
              routes: results.flatMap((q) => q.data?.routes ?? []),
              extent: mergeExtents(results.map((q) => q.data?.extent)),
            } satisfies RoutesBulkResult)
          : undefined,
      isLoading: results.some((q) => q.isLoading),
      isFetching: results.some((q) => q.isFetching),
      error: results.find((q) => q.error)?.error,
      // Which *chunk* failed; mapped back to slugs below, where `slugChunks` is in scope without
      // making `combine` close over a value react-query doesn't track.
      failedChunks: results.map((q) => !!q.error),
    }),
  })

  const failedSlugs = useMemo(() => {
    const failed = new Set<string>()
    slugChunks.forEach((slugChunk, i) => {
      if (failedChunks[i]) {
        for (const slug of slugChunk) failed.add(slug)
      }
    })
    return failed
  }, [slugChunks, failedChunks])

  return { data, isLoading, isFetching, error, failedSlugs }
}
