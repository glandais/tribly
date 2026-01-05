import { Navigate, useLocation } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useAuth } from '../../hooks/useAuth'
import { LoadingPage } from '../common/LoadingSpinner'
import { paths } from '@/config/paths'

interface ProtectedRouteProps {
  children: React.ReactNode
  requireAuth?: boolean
  redirectTo?: string
}

export function ProtectedRoute({
  children,
  requireAuth = true,
  redirectTo = paths.login(),
}: ProtectedRouteProps) {
  const { t } = useTranslation()
  const location = useLocation()
  const { isAuthenticated, isLoading } = useAuth()

  if (isLoading) {
    return <LoadingPage message={t('status.checkingAuth')} />
  }

  if (requireAuth && !isAuthenticated) {
    return <Navigate to={redirectTo} state={{ from: location }} replace />
  }

  if (!requireAuth && isAuthenticated) {
    const from = location.state?.from?.pathname || paths.home()
    return <Navigate to={from} replace />
  }

  return <>{children}</>
}

export function AuthenticatedRoute({ children }: { children: React.ReactNode }) {
  return <ProtectedRoute requireAuth>{children}</ProtectedRoute>
}

export function UnauthenticatedRoute({ children }: { children: React.ReactNode }) {
  return (
    <ProtectedRoute requireAuth={false} redirectTo="/">
      {children}
    </ProtectedRoute>
  )
}
