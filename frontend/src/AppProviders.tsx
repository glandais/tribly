import type { ReactNode } from 'react'
import type { i18n as I18nInstance } from 'i18next'
import { I18nextProvider } from 'react-i18next'
import { MantineProvider } from '@mantine/core'
import { Notifications } from '@mantine/notifications'
import { QueryClientProvider, type QueryClient } from '@tanstack/react-query'
import { theme } from './lib/theme'

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
}: {
  i18n: I18nInstance
  queryClient: QueryClient
  children: ReactNode
}) {
  return (
    <I18nextProvider i18n={i18n}>
      <MantineProvider theme={theme} defaultColorScheme="auto">
        <Notifications position="top-right" />
        <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
      </MantineProvider>
    </I18nextProvider>
  )
}
