import { useEffect } from 'react'
import { BrowserRouter } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Center, Loader, Stack, Text } from '@mantine/core'
import { ErrorBoundary } from './components/common/ErrorBoundary'
import { AppRoutes } from './config/RouteGenerator'
import { useAuthStore } from './store/authStore'
import { useAuth } from './hooks/useAuth'

function App() {
  const isInitialized = useAuthStore((state) => state.isInitialized)
  const initialize = useAuthStore((state) => state.initialize)
  const { t } = useTranslation()

  // Initialize Keycloak auth on mount
  useEffect(() => {
    initialize()
  }, [initialize])

  // useAuth triggers the /me query and sets isLoading to false when done
  const { isLoading } = useAuth()

  // Wait for auth initialization and user sync before rendering routes
  if (!isInitialized || isLoading) {
    return (
      <Center mih="100vh" bg="gray.0">
        <Stack align="center">
          <Loader color="indigo" size="lg" />
          <Text c="dimmed">{t('status.checkingAuth')}</Text>
        </Stack>
      </Center>
    )
  }

  return (
    <ErrorBoundary>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </ErrorBoundary>
  )
}

export default App
