import { useLocation, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useTeam } from './useTeam'
import { useRide } from './useRide'
import { useRoute } from './useRoute'
import type { BreadcrumbItem } from '../components/common/Breadcrumb'

export function useBreadcrumb(): BreadcrumbItem[] {
  const location = useLocation()
  const { teamSlug, rideSlug, routeId } = useParams<{
    teamSlug?: string
    rideSlug?: string
    routeId?: string
  }>()
  const { t: tCommon } = useTranslation('common')
  const { t: tRides } = useTranslation('rides')

  // Fetch team data if we're on a team-related route
  const { data: team } = useTeam(teamSlug)

  // Fetch ride data if we're on a ride-related route
  const { data: ride } = useRide(teamSlug, rideSlug)

  // Fetch route data if we're on a route-related route
  const { data: route } = useRoute(teamSlug, routeId)

  const items: BreadcrumbItem[] = []

  // Home page - no breadcrumb
  if (location.pathname === '/') {
    return items
  }

  // Teams list page
  if (location.pathname === '/teams') {
    items.push({ label: tCommon('nav.teams') })
    return items
  }

  // My teams page
  if (location.pathname === '/myteams') {
    items.push({ label: tCommon('nav.myTeams') })
    return items
  }

  // Profile page
  if (location.pathname.startsWith('/profile')) {
    items.push({ label: tCommon('nav.profile') })
    return items
  }

  // Team-related pages
  if (teamSlug) {
    // Teams root
    items.push({ label: tCommon('nav.teams'), path: '/teams' })

    // Team name
    if (team) {
      items.push({ label: team.name, path: `/teams/${teamSlug}` })
    }

    // Team settings
    if (location.pathname === `/teams/${teamSlug}/settings`) {
      items.push({ label: tCommon('nav.settings') })
      return items
    }

    // Team members
    if (location.pathname === `/teams/${teamSlug}/members`) {
      items.push({ label: tCommon('nav.members') })
      return items
    }

    // Rides section
    if (location.pathname.includes('/rides')) {
      items.push({ label: tRides('breadcrumb.rides'), path: `/teams/${teamSlug}/rides` })

      // New ride
      if (location.pathname === `/teams/${teamSlug}/rides/new`) {
        items.push({ label: tCommon('actions.new') })
        return items
      }

      // Ride detail or edit
      if (rideSlug && ride) {
        items.push({ label: ride.title, path: `/teams/${teamSlug}/rides/${rideSlug}` })

        // Edit ride
        if (location.pathname === `/teams/${teamSlug}/rides/${rideSlug}/edit`) {
          items.push({ label: tCommon('buttons.edit') })
        }
      }

      return items
    }

    // Routes section
    if (location.pathname.includes('/routes')) {
      items.push({ label: tCommon('nav.routes'), path: `/teams/${teamSlug}/routes` })

      // New route
      if (location.pathname === `/teams/${teamSlug}/routes/new`) {
        items.push({ label: tCommon('actions.new') })
        return items
      }

      // Route detail or edit
      if (routeId && route) {
        items.push({ label: route.name, path: `/teams/${teamSlug}/routes/${routeId}` })

        // Edit route
        if (location.pathname === `/teams/${teamSlug}/routes/${routeId}/edit`) {
          items.push({ label: tCommon('buttons.edit') })
        }
      }

      return items
    }
  }

  return items
}
