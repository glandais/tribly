import React from 'react'
import { prerenderToNodeStream } from 'react-dom/static'
import {
  createStaticHandler,
  createStaticRouter,
  StaticRouterProvider,
  parsePath,
  createPath,
  type Location,
} from 'react-router-dom'
import { dehydrate } from '@tanstack/react-query'
import { AppProviders } from './AppProviders'
import { AppFrame } from './App'
import { buildRoutes } from './config/RouteGenerator'
import { makeQueryClient } from './lib/queryClient'
import { createServerI18n, supportedLanguages } from './i18n'
import { requestContext, type SsrRequestStore } from './lib/requestContext'
import { setStoreGetter } from './lib/ssrContext'
import { resolveSsrSession } from './lib/ssrSession'
import { getConfig, getGetConfigQueryKey } from './api/endpoints/configuration/configuration'
import { getGetMeQueryKey } from './api/endpoints/users/users'
import { getPinnedTeamSlug } from './config/appConfig'
import { toRouter, toBrowser } from './config/pinnedHistory'
import type { Locale } from './config/paths'
import { buildMetaTags, type RouteMeta, type RouteMetaContext, type RouteMetaFn } from './lib/seo'
import type { RouteParams } from './config/routes.types'

// Bridge the SSR per-request store (AsyncLocalStorage) to the client-safe getter used by
// axiosInstance / appConfig / locale-context. Called once at module load.
setStoreGetter(() => requestContext.getStore())

export async function render(url: string, headers: Record<string, string> = {}) {
  // Resolve the request locale from Accept-Language (first token, language part only).
  const acceptLanguage = headers['accept-language'] || 'fr'
  const requestedLang = acceptLanguage.split(',')[0].split('-')[0].toLowerCase() || 'fr'
  const locale: Locale = (supportedLanguages as readonly string[]).includes(requestedLang)
    ? (requestedLang as Locale)
    : 'fr'
  if (locale !== requestedLang) {
    console.warn(`[SSR] Unsupported language "${requestedLang}", falling back to "fr"`)
  }

  const store: SsrRequestStore = { headers, locale, config: undefined, auth: undefined }

  return requestContext.run(store, async () => {
    const queryClient = makeQueryClient({ isServer: true })
    try {
      const i18nInstance = await createServerI18n(locale)

      // Two independent lookups, run concurrently so the session costs no extra serial round-trip:
      //
      // - the per-request config, tenant-resolved from the forwarded headers. It must land in the
      //   store BEFORE buildRoutes so isSingleTeam()/getPinnedTeamSlug() see it, and in the
      //   dehydrated state so the client reuses it instead of re-fetching.
      // - the visitor's session, if the request carried a refresh_token cookie. Everything the
      //   route loaders prefetch below then goes out authenticated.
      //
      // The config request goes out before the session is known, i.e. anonymously. That is correct:
      // the domain config is auth-independent (App.tsx excludes it from the post-login refetch for
      // exactly that reason). Both failures are non-fatal — a failed session simply renders the
      // page anonymously, as it always did.
      const [configResult, session] = await Promise.all([
        queryClient
          .fetchQuery({ queryKey: getGetConfigQueryKey(), queryFn: () => getConfig() })
          .catch((err) => {
            console.error('[SSR] Failed to load config:', err)
            return undefined
          }),
        resolveSsrSession(headers),
      ])
      store.config = configResult
      store.auth = session

      // /api/auth/refresh already returned the user, so seed the /me cache with it rather than
      // letting useAuth() fetch the same record again right after hydration.
      if (session?.user) {
        queryClient.setQueryData(getGetMeQueryKey(), session.user)
      }

      const routes = buildRoutes(queryClient)
      const handler = createStaticHandler(routes)

      // Reconstruct the origin from proxy headers (set by server.js) so the static handler gets a
      // valid absolute URL.
      const origin = headers['x-forwarded-proto']
        ? `${headers['x-forwarded-proto']}://${headers['x-forwarded-host'] || headers['host'] || 'localhost'}`
        : `http://${headers['host'] || 'localhost'}`

      // Pinned single-team host: the app operates internally on team-prefixed router paths while the
      // browser sees clean, unprefixed ones. Translate the incoming pathname browser→router before
      // the handler runs (mirroring the client's pinned history).
      const pinned = getPinnedTeamSlug()
      const sepIdx = url.search(/[?#]/)
      const pathname = sepIdx === -1 ? url : url.slice(0, sepIdx)
      const suffix = sepIdx === -1 ? '' : url.slice(sepIdx)
      const routerUrl = pinned ? toRouter(pathname) + suffix : url

      const context = await handler.query(
        new Request(`${origin}${routerUrl}`, { headers: new Headers(headers) })
      )

      // A Response means the router wants to redirect. Map its Location back to browser space on a
      // pinned host so the browser follows the clean URL.
      if (context instanceof Response) {
        const location = context.headers.get('Location') || '/'
        const mapped = pinned ? mapPathname(location, toBrowser) : location
        return { redirect: mapped, statusCode: context.status }
      }

      const router = createStaticRouter(handler.dataRoutes, context)

      // Pinned host: server-rendered <a href> must be browser-space. createStaticRouter emits
      // router-space (prefixed) hrefs; wrap createHref to strip the pinned-team prefix so the markup
      // matches what the client's pinned history produces (avoiding a hydration href mismatch).
      if (pinned) {
        const originalCreateHref = router.createHref.bind(router)
        router.createHref = (to: Location | URL) => mapPathname(originalCreateHref(to), toBrowser)
      }

      // The '*' catch-all matches unknown URLs, so the handler reports 200 for them; crawlers
      // must see a real 404 for the NotFound page.
      const leafMatch = context.matches[context.matches.length - 1]
      const statusCode = leafMatch?.route.path === '*' ? 404 : context.statusCode || 200

      // Build the server-rendered link-preview <head> block. meta() reads the per-request cache the
      // loaders just populated; `pathname` is the browser-space (clean) canonical URL base, so
      // pinned single-team hosts get unprefixed og:url. The block is injected at <!--ssr-head-->.
      const metaCtx: RouteMetaContext = {
        queryClient,
        params: (leafMatch?.params ?? {}) as RouteParams,
        origin,
        path: pathname,
        locale,
        config: store.config,
        t: i18nInstance.t,
      }
      let routeMeta: RouteMeta | undefined
      try {
        const metaFn = (leafMatch?.route.handle as { meta?: RouteMetaFn } | undefined)?.meta
        routeMeta = metaFn?.(metaCtx)
      } catch (metaErr) {
        console.error(`[SSR] meta() failed for ${url}:`, metaErr)
      }
      const head = buildMetaTags(routeMeta, metaCtx)

      const html = await renderAppToString(
        <React.StrictMode>
          <AppProviders i18n={i18nInstance} queryClient={queryClient}>
            <AppFrame>
              <StaticRouterProvider router={router} context={context} />
            </AppFrame>
          </AppProviders>
        </React.StrictMode>
      )

      const dehydratedState = dehydrate(queryClient)

      // The session travels to the client so its first render matches this markup exactly (and so
      // it can skip the boot-time /api/auth/refresh). server.js serialises it into a response that
      // is Cache-Control: no-store and Vary: Cookie — both are load-bearing, not cosmetic.
      return { html, dehydratedState, auth: store.auth, statusCode, lang: locale, head }
    } catch (err) {
      console.error(`[SSR] render failed for ${url}:`, err)
      throw err
    } finally {
      queryClient.clear()
    }
  })
}

/**
 * Render to a complete HTML string, waiting for lazy route chunks and suspended data.
 *
 * renderToString flushes synchronously: React.lazy pages (all our routes are lazy) are still
 * pending at flush time, so every page would emit its Suspense fallback instead of content.
 * The static prerender API waits for the whole tree and inlines all Suspense content — no
 * streaming placeholders or relocation scripts — which is what crawlers should see; the
 * output hydrates with hydrateRoot like any server render. On timeout the render is aborted
 * and still-pending boundaries degrade to their fallbacks (client renders them after hydration).
 */
async function renderAppToString(app: React.ReactElement, timeoutMs = 10_000): Promise<string> {
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(new Error('[SSR] render timed out')), timeoutMs)
  try {
    const { prelude } = await prerenderToNodeStream(app, {
      signal: controller.signal,
      // Fizz outlines completed Suspense boundaries larger than this (placeholder + hidden
      // segment + relocation script) so streaming can paint the shell early. We flush once,
      // so outlining only hurts: raise the threshold so all content is emitted inline.
      progressiveChunkSize: Number.MAX_SAFE_INTEGER,
      onError(err) {
        // Boundary-level errors are recoverable (the boundary falls back and hydrates
        // client-side); log and let the render finish.
        console.error('[SSR] render error:', err)
      },
    })
    let html = ''
    for await (const chunk of prelude) {
      html += chunk
    }
    return html
  } finally {
    clearTimeout(timer)
  }
}

/** Map only the pathname portion of a href string, preserving search/hash. */
function mapPathname(href: string, mapFn: (pathname: string) => string): string {
  const parsed = parsePath(href)
  return createPath({ ...parsed, pathname: mapFn(parsed.pathname ?? '/') })
}
