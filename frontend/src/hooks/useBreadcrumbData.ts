import { useGetTeam } from '../api/endpoints/teams/teams'
import { useGetRide } from '../api/endpoints/rides/rides'
import { useGetTrip } from '../api/endpoints/trips/trips'
import { useGetRoute } from '../api/endpoints/routes/routes'
import { useGetTemplate } from '../api/endpoints/ride-templates/ride-templates'
import { useGetAd } from '../api/endpoints/ads/ads'
import { useGetPost } from '../api/endpoints/posts/posts'
import { useGetPage } from '../api/endpoints/team-pages/team-pages'
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
  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })

  // Fetch entities conditionally based on params
  const { data: ride, isLoading: isLoadingRide } = useGetRide(teamSlug!, rideSlug!, {
    query: { enabled: !!teamSlug && !!rideSlug },
  })
  const { data: post, isLoading: isLoadingPost } = useGetPost(teamSlug!, postSlug!, {
    query: { enabled: !!teamSlug && !!postSlug },
  })
  const { data: trip, isLoading: isLoadingTrip } = useGetTrip(teamSlug!, tripSlug!, {
    query: { enabled: !!teamSlug && !!tripSlug },
  })
  const { data: route, isLoading: isLoadingRoute } = useGetRoute(teamSlug!, routeSlug!, {
    query: { enabled: !!teamSlug && !!routeSlug },
  })
  const { data: rideTemplate, isLoading: isLoadingTemplate } = useGetTemplate(
    teamSlug!,
    templateSlug!,
    { query: { enabled: !!teamSlug && !!templateSlug } }
  )
  const { data: ad, isLoading: isLoadingAd } = useGetAd(teamSlug!, adSlug!, {
    query: { enabled: !!teamSlug && !!adSlug },
  })
  const { data: teamPage, isLoading: isLoadingTeamPage } = useGetPage(teamSlug!, pageSlug!, {
    query: { enabled: !!teamSlug && !!pageSlug },
  })

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
