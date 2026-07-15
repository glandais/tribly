import React from 'react'
import ReactDOMServer from 'react-dom/server'
import {
  createStaticHandler,
  createStaticRouter,
  StaticRouterProvider,
  parsePath,
  createPath,
  type Location,
} from 'react-router-dom'
import { QueryClientProvider, dehydrate } from '@tanstack/react-query'
import { I18nextProvider } from 'react-i18next'
import { MantineProvider } from '@mantine/core'
import { buildRoutes } from './config/RouteGenerator'
import { makeQueryClient } from './lib/queryClient'
import { theme } from './lib/theme'
import { createServerI18n, supportedLanguages } from './i18n'
import { requestContext, type SsrRequestStore } from './lib/requestContext'
import { setStoreGetter } from './lib/ssrContext'
import { getConfig, getGetConfigQueryKey } from './api/endpoints/configuration/configuration'
import { getPinnedTeamSlug } from './config/appConfig'
import { toRouter, toBrowser } from './config/pinnedHistory'
import type { Locale } from './config/paths'

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

  const store: SsrRequestStore = { headers, locale, config: undefined }

  return requestContext.run(store, async () => {
    const queryClient = makeQueryClient({ isServer: true })
    try {
      const i18nInstance = await createServerI18n(locale)

      // Per-request config: anonymous, tenant-resolved from the forwarded headers. It must land in
      // the store BEFORE buildRoutes so isSingleTeam()/getPinnedTeamSlug() see it, and in the
      // dehydrated state so the client reuses it instead of re-fetching. Failure is non-fatal.
      try {
        store.config = await queryClient.fetchQuery({
          queryKey: getGetConfigQueryKey(),
          queryFn: () => getConfig(),
        })
      } catch (err) {
        console.error('[SSR] Failed to load config:', err)
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

      const statusCode = context.statusCode || 200

      const html = ReactDOMServer.renderToString(
        <React.StrictMode>
          <I18nextProvider i18n={i18nInstance}>
            <MantineProvider theme={theme} defaultColorScheme="auto">
              <QueryClientProvider client={queryClient}>
                <StaticRouterProvider router={router} context={context} />
              </QueryClientProvider>
            </MantineProvider>
          </I18nextProvider>
        </React.StrictMode>
      )

      const dehydratedState = dehydrate(queryClient)

      return { html, dehydratedState, statusCode, lang: locale }
    } catch (err) {
      console.error(`[SSR] render failed for ${url}:`, err)
      throw err
    } finally {
      queryClient.clear()
    }
  })
}

/** Map only the pathname portion of a href string, preserving search/hash. */
function mapPathname(href: string, mapFn: (pathname: string) => string): string {
  const parsed = parsePath(href)
  return createPath({ ...parsed, pathname: mapFn(parsed.pathname ?? '/') })
}
