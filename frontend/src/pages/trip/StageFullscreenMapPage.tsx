import { useEffect } from 'react'
import { Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetTrip } from '@/api/endpoints/trips/trips'
import { useGetRoute } from '@/api/endpoints/routes/routes'
import { RouteFullscreenView } from '@/components/route/RouteFullscreenView'
import { LoadingPage } from '@/components/common/LoadingSpinner'
import { paths } from '@/config/paths'
import { useAppName } from '@/hooks/useAppName'
import { useCanonicalPath } from '@/hooks/useCanonicalPath'

/**
 * Fullscreen map for a trip stage's route (`.../map`). Bare layout (no AppShell), so it owns its own
 * `document.title`. Mirrors {@link StageDetailPage}'s data flow (`useGetTrip` → stage → `useGetRoute`);
 * a stage with no route falls back to the stage detail page.
 */
export function StageFullscreenMapPage() {
  const { teamSlug, tripSlug, stageSlug } = useParams<{
    teamSlug: string
    tripSlug: string
    stageSlug: string
  }>()
  const { t } = useTranslation()
  const appName = useAppName()

  const { data: team } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: trip, isLoading: isLoadingTrip } = useGetTrip(teamSlug!, tripSlug!, {
    query: { enabled: !!teamSlug && !!tripSlug },
  })

  // Find stage and its route slug (derived before hooks to maintain consistent hook order)
  const stage = trip?.stages?.find((s) => s.slug === stageSlug)
  const routeSlug = stage?.route?.slug

  const { data: route, isLoading: isLoadingRoute } = useGetRoute(
    teamSlug!,
    routeSlug ?? '',
    undefined,
    {
      query: { enabled: !!teamSlug && !!routeSlug },
    }
  )

  useCanonicalPath(
    team && trip && stageSlug ? paths.stageMap(team.slug, trip.slug, stageSlug) : undefined
  )

  const title = stage?.name ?? route?.name ?? ''
  useEffect(() => {
    if (title) {
      document.title = appName ? `${title} — ${appName}` : title
    }
  }, [title, appName])

  if (isLoadingTrip || (routeSlug && isLoadingRoute)) {
    return <LoadingPage message={t('loading')} />
  }

  // No trip, no stage, or a stage without a route → back to the stage detail page.
  if (!team || !trip || !stage || !route) {
    return <Navigate to={paths.stage(teamSlug!, tripSlug!, stageSlug!)} replace />
  }

  return (
    <RouteFullscreenView
      route={route}
      title={title}
      backTo={paths.stage(team.slug, trip.slug, stageSlug!)}
    />
  )
}
