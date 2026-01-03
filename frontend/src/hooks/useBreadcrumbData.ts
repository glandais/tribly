import { useTeam } from './useTeam'
import { useRide } from './useRide'
import { usePost } from './usePost'
import { useTrip } from './useTrip'
import { useRoute } from './useRoute'
import { useRideTemplate } from './useRideTemplate'
import { useAd } from './useAd'
import { useTeamPage } from './useTeamPages'
import type { RouteParams, EntityType } from '../config/routes.types'

interface EntityData {
  name: string | undefined
  isLoading: boolean
}

/**
 * Hook to fetch entity names for breadcrumbs
 * Only fetches data for entities present in params
 */
export function useBreadcrumbData(params: RouteParams): Record<EntityType, EntityData> {
  const { teamSlug, rideSlug, postSlug, tripSlug, routeSlug, templateSlug, adSlug, pageSlug } =
    params

  // Fetch team - needed for all team-related breadcrumbs
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)

  // Fetch entities conditionally based on params
  const { data: ride, isLoading: isLoadingRide } = useRide(teamSlug, rideSlug)
  const { data: post, isLoading: isLoadingPost } = usePost(teamSlug, postSlug)
  const { data: trip, isLoading: isLoadingTrip } = useTrip(teamSlug, tripSlug)
  const { data: route, isLoading: isLoadingRoute } = useRoute(teamSlug, routeSlug)
  const { data: rideTemplate, isLoading: isLoadingTemplate } = useRideTemplate(
    teamSlug,
    templateSlug
  )
  const { data: ad, isLoading: isLoadingAd } = useAd(teamSlug, adSlug)
  const { data: teamPage, isLoading: isLoadingTeamPage } = useTeamPage(teamSlug, pageSlug)

  return {
    team: { name: team?.name, isLoading: isLoadingTeam },
    ride: { name: ride?.name, isLoading: isLoadingRide },
    post: { name: post?.name, isLoading: isLoadingPost },
    trip: { name: trip?.name, isLoading: isLoadingTrip },
    route: { name: route?.name, isLoading: isLoadingRoute },
    rideTemplate: { name: rideTemplate?.name, isLoading: isLoadingTemplate },
    ad: { name: ad?.name, isLoading: isLoadingAd },
    teamPage: { name: teamPage?.title, isLoading: isLoadingTeamPage },
  }
}
