import { StrictMode } from 'react'
import { createRoot, hydrateRoot } from 'react-dom/client'
import { hydrate, type DehydratedState } from '@tanstack/react-query'
import type { HydrationState } from 'react-router-dom'
import App from './App'
import { AppProviders } from './AppProviders'
import { makeQueryClient } from './lib/queryClient'
import { fetchAppConfig, seedAppConfig } from './config/appConfig'
import { getGetConfigQueryKey } from './api/endpoints/configuration/configuration'
import i18n, { i18nReady } from './i18n'
import type { ConfigDto } from './api/dto'
import { hydrateAuthFromSSR } from './store/authStore'
import type { SsrAuthSnapshot } from './lib/requestContext'
import './index.css'

declare global {
  interface Window {
    __REACT_QUERY_STATE__?: DehydratedState
    /** The session the server rendered with, when the request carried one. */
    __AUTH_STATE__?: SsrAuthSnapshot
    /** Injected by StaticRouterProvider in the SSR markup. */
    __staticRouterHydrationData?: HydrationState
  }
}

async function bootstrap() {
  // The initial client render must match the server markup, so i18n has to be ready first.
  await i18nReady

  const queryClient = makeQueryClient()
  const dehydratedState = window.__REACT_QUERY_STATE__

  // Hydrate the server-dehydrated cache BEFORE the router is created: buildRoutes()/getPinnedHistory
  // read config synchronously via getAppConfig(), so the config must already be present here.
  if (dehydratedState) {
    hydrate(queryClient, dehydratedState)
  }

  // Adopt the server's session before anything renders. Without this the first client render would
  // be anonymous while the markup is not, which is a hydration mismatch on every page — and the app
  // would re-fetch a session it already has.
  if (window.__AUTH_STATE__) {
    hydrateAuthFromSSR(window.__AUTH_STATE__)
  }

  // The config drives single-team rooting and the pinned-team history, so it must be resolvable
  // synchronously before the first render. Prefer the dehydrated cache; fall back to a fetch.
  try {
    const config =
      queryClient.getQueryData<ConfigDto>(getGetConfigQueryKey()) ?? (await fetchAppConfig())
    seedAppConfig(config)
    queryClient.setQueryData(getGetConfigQueryKey(), config)
  } catch (error) {
    console.error('Failed to load app config', error)
  }

  const root = document.getElementById('root')
  if (!root) {
    throw new Error(
      '[SSR] Fatal: #root element not found in document. Cannot mount React application.'
    )
  }

  const app = (
    <StrictMode>
      <AppProviders i18n={i18n} queryClient={queryClient}>
        <App queryClient={queryClient} />
      </AppProviders>
    </StrictMode>
  )

  if (dehydratedState) {
    hydrateRoot(root, app, {
      onRecoverableError: (error, errorInfo) => {
        console.error('[hydration] Recoverable error:', error, errorInfo?.componentStack || '')
      },
    })
  } else {
    createRoot(root).render(app)
  }
}

void bootstrap()
