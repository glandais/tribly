import { useLocation, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useTeam } from './useTeam'
import { useRide } from './useRide'
import { useRideTemplate } from './useRideTemplate'
import { useTrip } from './useTrip'
import { usePost } from './usePost'
import { useRoute } from './useRoute'
import type { BreadcrumbItemType } from '../components/common/Breadcrumb'

export function useBreadcrumb(): BreadcrumbItemType[] {
  const location = useLocation()
  const { teamSlug, rideSlug, templateSlug, tripSlug, postSlug, routeSlug } = useParams<{
    teamSlug?: string
    rideSlug?: string
    templateSlug?: string
    tripSlug?: string
    postSlug?: string
    routeSlug?: string
  }>()
  const { t: tCommon } = useTranslation('common')
  const { t: tTeams } = useTranslation('teams')
  const { t: tRides } = useTranslation('rides')
  const { t: tRideTemplates } = useTranslation('rideTemplates')
  const { t: tTrips } = useTranslation('trips')
  const { t: tPosts } = useTranslation('posts')

  // Fetch team data if we're on a team-related route
  const { data: team } = useTeam(teamSlug)

  // Fetch ride data if we're on a ride-related route
  const { data: ride } = useRide(teamSlug, rideSlug)

  // Fetch ride template data if we're on a ride-template-related route
  const { data: rideTemplate } = useRideTemplate(teamSlug, templateSlug)

  // Fetch trip data if we're on a trip-related route
  const { data: trip } = useTrip(teamSlug, tripSlug)

  // Fetch post data if we're on a post-related route
  const { data: post } = usePost(teamSlug, postSlug)

  // Fetch route data if we're on a route-related route
  const { data: route } = useRoute(teamSlug, routeSlug)

  const items: BreadcrumbItemType[] = []

  // Home page - no breadcrumb
  if (location.pathname === '/') {
    return items
  }

  // Teams list page
  if (location.pathname === '/teams') {
    items.push({ label: tCommon('nav.teams') })
    return items
  }

  // Create team page
  if (location.pathname === '/teams/new') {
    items.push({ label: tCommon('nav.teams'), path: '/teams' })
    items.push({ label: tTeams('create.title') })
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
    if (location.pathname === `/teams/${teamSlug}/edit`) {
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
        items.push({ label: ride.name, path: `/teams/${teamSlug}/rides/${rideSlug}` })

        // Edit ride
        if (location.pathname === `/teams/${teamSlug}/rides/${rideSlug}/edit`) {
          items.push({ label: tCommon('buttons.edit') })
        }
      }

      return items
    }

    // Ride Templates section
    if (location.pathname.includes('/ride-templates')) {
      items.push({
        label: tRideTemplates('breadcrumb.rideTemplates'),
        path: `/teams/${teamSlug}/ride-templates`,
      })

      // New ride template
      if (location.pathname === `/teams/${teamSlug}/ride-templates/new`) {
        items.push({ label: tCommon('actions.new') })
        return items
      }

      // Edit ride template
      if (templateSlug && rideTemplate) {
        items.push({
          label: rideTemplate.name,
          path: `/teams/${teamSlug}/ride-templates/${templateSlug}/edit`,
        })
        items.push({ label: tCommon('buttons.edit') })
      }

      return items
    }

    // Trips section
    if (location.pathname.includes('/trips')) {
      items.push({ label: tTrips('breadcrumb.trips'), path: `/teams/${teamSlug}/trips` })

      // New trip
      if (location.pathname === `/teams/${teamSlug}/trips/new`) {
        items.push({ label: tCommon('actions.new') })
        return items
      }

      // Trip detail or edit
      if (tripSlug && trip) {
        items.push({ label: trip.name, path: `/teams/${teamSlug}/trips/${tripSlug}` })

        // Edit trip
        if (location.pathname === `/teams/${teamSlug}/trips/${tripSlug}/edit`) {
          items.push({ label: tCommon('buttons.edit') })
        }
      }

      return items
    }

    // Posts section
    if (location.pathname.includes('/posts')) {
      items.push({ label: tPosts('breadcrumb.posts'), path: `/teams/${teamSlug}/posts` })

      // New post
      if (location.pathname === `/teams/${teamSlug}/posts/new`) {
        items.push({ label: tCommon('actions.new') })
        return items
      }

      // Post detail or edit
      if (postSlug && post) {
        items.push({ label: post.name, path: `/teams/${teamSlug}/posts/${postSlug}` })

        // Edit post
        if (location.pathname === `/teams/${teamSlug}/posts/${postSlug}/edit`) {
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
      if (routeSlug && route) {
        items.push({ label: route.name, path: `/teams/${teamSlug}/routes/${routeSlug}` })

        // Edit route
        if (location.pathname === `/teams/${teamSlug}/routes/${routeSlug}/edit`) {
          items.push({ label: tCommon('buttons.edit') })
        }
      }

      return items
    }
  }

  return items
}
