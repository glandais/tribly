import { StrictMode } from 'react'
import { createRoot, hydrateRoot } from 'react-dom/client'
import { QueryClientProvider, hydrate, type DehydratedState } from '@tanstack/react-query'
import { MantineProvider } from '@mantine/core'
import { Notifications } from '@mantine/notifications'
import App from './App'
import { makeQueryClient } from './lib/queryClient'
import { theme } from './lib/theme'
import { fetchAppConfig, seedAppConfig } from './config/appConfig'
import { getGetConfigQueryKey } from './api/endpoints/configuration/configuration'
import { i18nReady } from './i18n'
import type { ConfigDto } from './api/dto'
import './index.css'

declare global {
  interface Window {
    __REACT_QUERY_STATE__?: DehydratedState
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
      <MantineProvider theme={theme} defaultColorScheme="auto">
        <Notifications position="top-right" />
        <QueryClientProvider client={queryClient}>
          <App queryClient={queryClient} />
        </QueryClientProvider>
      </MantineProvider>
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
