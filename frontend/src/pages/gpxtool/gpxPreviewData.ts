import type { QueryClient } from '@tanstack/react-query'
import { useGetPreview, prefetchGetPreviewQuery } from '@/api/endpoints/gpx-previews/gpx-previews'

/**
 * The one description of what a GPX preview screen reads, consumed two ways: `GpxPreviewPage` and
 * `GpxPreviewFullscreenMapPage` both call {@link useGpxPreviewData} for the query result, the
 * `gpx-tools-view` and `gpx-tools-map` routes in `routes.config.ts` call {@link prefetchGpxPreview}
 * for the same data server-side. There is only one call on each side — no derivation, no params
 * builder — but the module still exists so the two sides can't drift: the pages are identical
 * consumers of the same preview, and keeping the fetch and its prefetch declared once, next to the
 * page, is the point (not the amount of code it saves).
 *
 * Its own module rather than exports of either page: `routes.config.ts` is imported eagerly and
 * must not pull a page out of its lazy chunk (same contract as `pages/ride/rideDetailData.ts`).
 *
 * Neither route is `teamScopedPrefetch`-wrapped: the link is public and shareable — holding the
 * unguessable preview id is enough to view it, there is no team or auth gate to resolve first.
 */
export function useGpxPreviewData(previewId?: string) {
  return useGetPreview(previewId!, { query: { enabled: !!previewId } })
}

/**
 * Server-side counterpart of {@link useGpxPreviewData}. Shared by both routes: the detail page and
 * its fullscreen map read the exact same preview, nothing more.
 */
export async function prefetchGpxPreview(
  queryClient: QueryClient,
  previewId: string
): Promise<void> {
  await prefetchGetPreviewQuery(queryClient, previewId)
}
