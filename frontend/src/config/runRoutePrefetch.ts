import Axios from 'axios'
import type { QueryClient } from '@tanstack/react-query'
import type { RouteConfig, RouteParams } from './routes.types'

/** Where a prefetch was triggered from — only used to make the log line readable. */
export type PrefetchSource = 'loader' | 'link'

/**
 * Run a route's `prefetch` and never throw.
 *
 * Used by BOTH the router loader (RouteGenerator — SSR and client navigation) and the hover/focus
 * path (`lib/prefetch.ts`). One body, so the two can't drift apart on which params they pass, on
 * whether they keep the query string, or on which failures are expected: it runs anonymous on the
 * server and pre-auth on the client, so 401/403/404 are normal and only warned. Everything else is
 * logged and swallowed — the caller proceeds and each component handles its own loading/error state.
 *
 * `url` carries the query string, and that matters as much as the path params: a list page reached
 * with filters (`?p=5`, `?q=col`) reads a different query key than the unfiltered default, so a
 * prefetch blind to it fills the cache with an entry the page never looks at — and the visitor gets
 * an empty list in the HTML. Shared links are exactly the case URL filters exist for.
 */
export async function runRoutePrefetch(
  config: RouteConfig,
  queryClient: QueryClient,
  params: RouteParams | Record<string, string | undefined>,
  url: URL,
  source: PrefetchSource
): Promise<void> {
  if (!config.prefetch) return

  // Filter out undefined values so prefetch functions receive guaranteed strings.
  const definedParams = Object.fromEntries(
    Object.entries(params).filter((entry): entry is [string, string] => entry[1] !== undefined)
  )

  try {
    await config.prefetch(queryClient, definedParams as RouteParams, url)
  } catch (err) {
    const isExpected =
      Axios.isAxiosError(err) && [401, 403, 404].includes(err.response?.status ?? 0)
    const logFn = isExpected ? console.warn : console.error
    logFn(
      `[prefetch:${source}] failed for route "${config.id}" url="${url.href}" params=${JSON.stringify(definedParams)}:`,
      err
    )
  }
}
