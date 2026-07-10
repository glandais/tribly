import { useEffect } from 'react'
import { BrowserRouter, unstable_HistoryRouter as HistoryRouter } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Center, Loader, Stack, Text } from '@mantine/core'
import { ErrorBoundary } from './components/common/ErrorBoundary'
import { AppRoutes } from './config/RouteGenerator'
import { useAuthStore } from './store/authStore'
import { useAuth } from './hooks/useAuth'
import { prefetchCommonRoutes } from './lib/prefetch'
import { getPinnedHistory } from './config/pinnedHistory'

function App() {
  const isInitialized = useAuthStore((state) => state.isInitialized)
  const initialize = useAuthStore((state) => state.initialize)
  const { t } = useTranslation()
  // useAuth triggers the /me query and sets isLoading to false when done
  const { isLoading } = useAuth()

  // Resolve the pinned-team history lazily here (not at module load): /api/config is only ready by
  // the time this renders. Memoized inside getPinnedHistory(), so it's a stable singleton. Null on
  // normal hosts → plain BrowserRouter.
  const pinnedHistory = getPinnedHistory()

  // Initialize auth on mount
  useEffect(() => {
    initialize()
  }, [initialize])

  // Prefetch common routes after app is ready
  useEffect(() => {
    if (isInitialized && !isLoading) {
      prefetchCommonRoutes()
    }
  }, [isInitialized, isLoading])

  // Wait for auth initialization and user sync before rendering routes
  if (!isInitialized || isLoading) {
    return (
      <Center mih="100vh" bg="var(--mantine-color-body)">
        <Stack align="center">
          <Loader color="primary" size="lg" />
          <Text c="dimmed">{t('status.checkingAuth')}</Text>
        </Stack>
      </Center>
    )
  }

  return (
    <ErrorBoundary>
      {pinnedHistory ? (
        <HistoryRouter history={pinnedHistory}>
          <AppRoutes />
        </HistoryRouter>
      ) : (
        <BrowserRouter>
          <AppRoutes />
        </BrowserRouter>
      )}
    </ErrorBoundary>
  )
}

export default App
