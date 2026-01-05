import { useEffect } from 'react'
import { BrowserRouter } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ArrowPathIcon } from '@heroicons/react/24/outline'
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
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <ArrowPathIcon className="inline-block h-8 w-8 animate-spin text-indigo-600" />
          <p className="mt-4 text-gray-600">{t('status.checkingAuth')}</p>
        </div>
      </div>
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
