/**
 * Route prefetching utilities
 *
 * Based on best practices from:
 * https://dev.to/yoskutik/how-to-make-your-app-indefinitely-lazy-part-4-preload-in-advance-2go1
 *
 * Prefetching downloads route chunks in advance when users hover/focus navigation links,
 * reducing perceived latency when they actually navigate.
 */

// Route import functions - must match routes.config.ts lazy imports
const routeImports = {
  // Home & Discovery
  home: () => import('../pages/home/HomePage'),
  allRoutes: () => import('../pages/route/AllRoutesPage'),

  // Auth
  login: () => import('../pages/auth/LoginPage'),
  profile: () => import('../pages/auth/UserProfilePage'),

  // Team
  teams: () => import('../pages/team/TeamListPage'),
  teamCreate: () => import('../pages/team/CreateTeamPage'),
  teamDetail: () => import('../pages/publication/PublicationListPage'),
  teamAbout: () => import('../pages/team/TeamAboutPage'),
  teamPage: () => import('../pages/team/TeamPageDetailPage'),
  teamAdmin: () => import('../pages/team/TeamAdminPage'),
  teamMembers: () => import('../pages/team/TeamMembersPage'),
  teamSettings: () => import('../pages/team/TeamSettingsPage'),
  teamPlaces: () => import('../pages/team/TeamPlacesPage'),
  teamPages: () => import('../pages/team/TeamPagesAdminPage'),
  teamPageCreate: () => import('../pages/team/CreateTeamPagePage'),
  teamPageEdit: () => import('../pages/team/EditTeamPagePage'),

  // Rides
  rideDetail: () => import('../pages/ride/RideDetailPage'),
  rideCreate: () => import('../pages/ride/CreateRidePage'),
  rideEdit: () => import('../pages/ride/EditRidePage'),

  // Ride Templates
  rideTemplates: () => import('../pages/ridetemplate/RideTemplateListPage'),
  rideTemplateCreate: () => import('../pages/ridetemplate/CreateRideTemplatePage'),
  rideTemplateEdit: () => import('../pages/ridetemplate/EditRideTemplatePage'),

  // Trips
  tripDetail: () => import('../pages/trip/TripDetailPage'),
  tripCreate: () => import('../pages/trip/CreateTripPage'),
  tripEdit: () => import('../pages/trip/EditTripPage'),
  stageDetail: () => import('../pages/trip/StageDetailPage'),

  // Posts
  postDetail: () => import('../pages/post/PostDetailPage'),
  postCreate: () => import('../pages/post/CreatePostPage'),
  postEdit: () => import('../pages/post/EditPostPage'),

  // Routes
  routes: () => import('../pages/route/RouteListPage'),
  routeDetail: () => import('../pages/route/RouteDetailPage'),
  routeCreate: () => import('../pages/route/CreateRoutePage'),
  routeEdit: () => import('../pages/route/EditRoutePage'),

  // Ads
  ads: () => import('../pages/ad/AdListPage'),
  adDetail: () => import('../pages/ad/AdDetailPage'),
  adCreate: () => import('../pages/ad/CreateAdPage'),
  adEdit: () => import('../pages/ad/EditAdPage'),
} as const

export type RouteKey = keyof typeof routeImports

// Track which routes have been prefetched to avoid duplicate requests
const prefetchedRoutes = new Set<RouteKey>()

/**
 * Prefetch a route's chunk
 * Safe to call multiple times - will only fetch once
 */
export function prefetchRoute(route: RouteKey): void {
  if (prefetchedRoutes.has(route)) {
    return
  }

  prefetchedRoutes.add(route)

  // Use requestIdleCallback if available, otherwise setTimeout
  const schedulePreload = window.requestIdleCallback || ((cb: () => void) => setTimeout(cb, 1))

  schedulePreload(() => {
    routeImports[route]().catch(() => {
      // Remove from set if prefetch fails, allowing retry
      prefetchedRoutes.delete(route)
    })
  })
}

/**
 * Prefetch multiple routes at once
 */
export function prefetchRoutes(routes: RouteKey[]): void {
  routes.forEach(prefetchRoute)
}

/**
 * Get event handlers for prefetching on hover/focus
 */
export function getPrefetchHandlers(route: RouteKey) {
  return {
    onMouseEnter: () => prefetchRoute(route),
    onFocus: () => prefetchRoute(route),
  }
}

/**
 * Prefetch common routes that users are likely to visit
 * Call this after the app is idle (e.g., after initial render)
 */
export function prefetchCommonRoutes(): void {
  // These are the most commonly visited routes
  const commonRoutes: RouteKey[] = [
    'teams',
    'teamDetail',
    'rideDetail',
    'routeDetail',
    'postDetail',
  ]

  // Delay prefetching to not compete with initial page load
  setTimeout(() => {
    prefetchRoutes(commonRoutes)
  }, 3000)
}
