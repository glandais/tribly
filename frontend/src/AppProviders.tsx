import type { ReactNode } from 'react'
import type { i18n as I18nInstance } from 'i18next'
import { I18nextProvider } from 'react-i18next'
import { MantineProvider, type MantineColorScheme } from '@mantine/core'
import { Notifications } from '@mantine/notifications'
import { QueryClientProvider, type QueryClient } from '@tanstack/react-query'
import { theme } from './lib/theme'
import { SsrColorSchemeContext } from './hooks/useResolvedColorScheme'

/**
 * Provider tree shared by entry-client and entry-server.
 *
 * The two entries MUST render the exact same component structure: React's useId derives ids
 * from tree position, so any server-only or client-only wrapper (even a context provider or a
 * null-rendering sibling) shifts every generated id below it and breaks hydration. Keep every
 * structural change to the providers in this single component.
 */
export function AppProviders({
  i18n,
  queryClient,
  children,
  defaultColorScheme = 'auto',
}: {
  i18n: I18nInstance
  queryClient: QueryClient
  children: ReactNode
  /** The signed-in visitor's stored theme preference, resolved per-request/at module load so the
   * first render (server or client) already matches it. Anonymous visitors keep `'auto'`, whose
   * `localStorage`-vs-`prefers-color-scheme` resolution is handled by Mantine's own manager and
   * `ColorSchemeSwitcher`'s post-hydration correction. */
  defaultColorScheme?: MantineColorScheme
}) {
  return (
    <I18nextProvider i18n={i18n}>
      <MantineProvider theme={theme} defaultColorScheme={defaultColorScheme}>
        <SsrColorSchemeContext.Provider value={defaultColorScheme === 'dark' ? 'dark' : 'light'}>
          <Notifications position="top-right" />
          <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
        </SsrColorSchemeContext.Provider>
      </MantineProvider>
    </I18nextProvider>
  )
}
